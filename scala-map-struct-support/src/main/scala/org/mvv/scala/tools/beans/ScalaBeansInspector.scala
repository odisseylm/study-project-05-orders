package org.mvv.scala.tools.beans

import scala.quoted.Quotes
import scala.tasty.inspector.{Inspector, Tasty, TastyInspector}
//
import org.mvv.scala.tools.replaceSuffix

import scala.annotation.nowarn
import scala.collection.mutable
import scala.quoted.Quotes
import scala.tasty.inspector.{ Inspector, Tasty, TastyInspector }
import java.net.{ URI, URL, URLClassLoader }
import java.nio.file.Path
//
import ClassKind.classKind
import org.mvv.scala.tools.Logger
import org.mvv.scala.mapstruct.debug.{ printFields, printSymbolInfo, printTreeSymbolInfo }
import org.mvv.scala.tools.{ Logger, replaceSuffix, fullName, tryDo, ifBlank, afterLastOr, afterLastOfAnyCharsOr }
import org.mvv.scala.tools.beans._Quotes.extractType
import org.mvv.scala.tools.quotes.{ topClassOrModuleFullName, fullPackageName, getFullClassName, refName, classExists, classFullPackageName }



private val classesToIgnore: Set[_Type] = Set(_Type.ObjectType, _Type("java.lang.Comparable"))
private val log: Logger = Logger(topClassOrModuleFullName)


class ScalaBeansInspector extends Inspector :

  // it contains ONLY 'normal' classes from input tasty file
  private val classesByFullName:  mutable.Map[String, _Class] = mutable.HashMap()
  private val processedTastyFiles: mutable.Map[String, List[_Class]] = mutable.Map()
  private val processedJars: mutable.Set[Path] = mutable.Set()

  private var classLoaders: Map[String, ClassLoader] = Map()

  def classesDescr: Map[String, _Class] = Map.from(classesByFullName)
  def classDescr(classFullName: String): Option[_Class] = classesByFullName.get(classFullName)

  def inspectClass(fullClassName: String): _Class = inspectClass(fullClassName, Nil*)

  def inspectClass(fullClassName: String, classLoaders: ClassLoader*): _Class =
    addClassLoaders(classLoaders*)
    inspectClass(loadClass(fullClassName, this.classLoaders.values))

  def inspectClassDef(using q: Quotes)(fullClassName: String): _Class =
    import q.reflect.*

    val alreadyProcessed = classesByFullName.get(fullClassName)
    if alreadyProcessed.isDefined then return alreadyProcessed.get

    require(classExists(fullClassName), s"Class [$fullClassName] is not found.")

    val clsSymbol = Symbol.classSymbol(fullClassName)
    val classDef = clsSymbol.tree.asInstanceOf[ClassDef]
    val classDefPackageName = classFullPackageName(classDef)
    val _package = FilePackageContainer(classDefPackageName)

    val clsList: List[_Class] = inspectClassDefImpl(classDef, _package, InspectMode.ScalaAST, ClassSource.MacroQuotes)
    clsList.foreach { cls => classesByFullName.put(cls.fullName, cls) }

    val thisCls = clsList.find(cls => cls.fullName == fullClassName).get

    val allParentRuntimeFullClassNames = clsList.flatMap(_.parentTypes).map(_.runtimeTypeName).distinct
    allParentRuntimeFullClassNames
      .foreach { parentClassFullName => inspectClassDef(parentClassFullName) }

    thisCls


  def inspectClass(cls: Class[?]): _Class =
    cls.classKind match
      case ClassKind.Java =>
        val res: _Class = inspectJavaClass(cls, this)
        classesByFullName.put(cls.getName.nn, res)
        res
      case ClassKind.Scala2 =>
        log.warn(s"scala2 class ${cls.getName} is processed as java class (sine scala2 format is not supported now).")
        val res: _Class = inspectJavaClass(cls, this)
        classesByFullName.put(cls.getName.nn, res)
        res
      case ClassKind.Scala3 =>
        val classLocation = getClassLocationUrl(cls)
        classLocation.getProtocol match
          case "file" => inspectTastyFile(fileUrlToPath(classLocation.toExternalForm.nn).toString).head
          case "jar"  => inspectJar(jarUrlToJarPath(classLocation))
            classesByFullName(cls.getName.nn)
          case _ => throw IllegalStateException(s"Unsupported class location [$classLocation].")

  private def addJarClassLoaderIfNeeded(jarPath: Path): Unit =
    if classLoaders.contains(jarPath.toString) then { return }

    val someJarClassFullNames: List[String] = getJarClassFullNames(jarPath, 20)
    val jarClassesAreAlreadyPresentInClassLoaders: Boolean = someJarClassFullNames
      .exists(className => tryToLoadClass(className, classLoaders.values).isDefined)

    if !jarClassesAreAlreadyPresentInClassLoaders then
      import scala.language.unsafeNulls
      classLoaders += (jarPath.toString, URLClassLoader(Array(jarPath.toUri.toURL)))

  // TODO: use them
  private def addClassLoaders(classLoaders: ClassLoader*): Unit =
    classLoaders.foreach { cl =>
      val alreadyAdded = this.classLoaders.values.exists(_ == cl)
      if !alreadyAdded then
        this.classLoaders += (cl.getName.nn, cl)
    }

  private def getJarClassFullNames(jarPath: Path, classCount: Int): List[String] =
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.ListHasAsScala

    val jarFile: java.util.jar.JarFile = java.util.jar.JarFile(jarPath.toFile.nn, false)
    val classList: java.util.List[String] = jarFile.stream()
      .filter { entry => val path = entry.getName
        path.endsWith(".class") && !path.contains("$") }
      .limit(classCount)
      .map(entry => entry.getName.stripSuffix(".class").replace('/', '.'))
      .toList
    List.from(classList.asScala)

  def inspectTastyFile(tastyOrClassFile: String): List[_Class] =
    val tastyFile = if tastyOrClassFile.endsWith(".tasty")
      then tastyOrClassFile
      else tastyOrClassFile.replaceSuffix(".class", ".tasty")
    TastyInspector.inspectTastyFiles(List(tastyFile))(this)
    this.processedTastyFiles.get(tastyFile)
      .map(_.toList) .getOrElse(List())

  def inspectJar(jarPath: Path): List[_Class] =
    processedJars.addOne(jarPath)

    addJarClassLoaderIfNeeded(jarPath)

    val before = this.classesDescr
    TastyInspector.inspectTastyFilesInJar(jarPath.toString)(this)
    val after = this.classesDescr

    val dif = after.values.toSet -- before.values.toSet
    dif.toList

  override def inspect(using q: Quotes)(beanType: List[Tasty[q.type]]): Unit =
    import q.reflect.*
    import org.mvv.scala.tools.safeSubString

    val dependenciesJars = mutable.Set[Path]()
    var allProcessedClasses: List[_Class] = Nil

    def logUnexpectedTreeEl(processor: TreeTraverser|String, treeEl: Tree): Unit =
      val logPrefix: String = processor match
        case s: String => s
        case p: TreeTraverser => p.simpleClassName
      log.warn(s"$logPrefix: Unexpected tree element!" +
        s" Please explicitly process or ignore element: $treeEl")


    class PackageTreeTraverser (
      val packageClause: PackageClause,
      val parentPackage: Option[FilePackageContainer],
      ) extends TreeTraverser {

      val _package: FilePackageContainer = FilePackageContainer(fullPackageName(packageClause))

      override def traverseTree(tree: Tree)(owner: Symbol): Unit =
        val logPrefix = s"${this.simpleClassName}: "
        try
          tree match
            // these simple approaches do not work
            //case this.packageClause.pid => // ignore
            //case _: this.packageClause.pid => // ignore

            case ident @ Ident(pid) if ident == this.packageClause.pid => // ignore

            case this.packageClause =>
              // process self tree
              super.traverseTree(tree)(owner)

            case p @ PackageClause(pid: Ref, _) => // hm... is it possible?? I think in scala, yes!
              log.debug(s"$logPrefix It is PackageClause ($pid): ${tree.shortContent}")
              PackageTreeTraverser(p, parentPackage).traverseTree(p)(p.symbol)

            case cd @ ClassDef(classDefName: String, _, _, _, _) =>
              log.debug(s"$logPrefix It is ClassDef ($classDefName): ${tree.shortContent}")
              val fullClassName = getFullClassName(cd)
              // ?? cd.symbol.fullName
              val classSrc = tryToLoadClass(fullClassName, classLoaders.values)
                .map(c => ClassSource.of(c))
                .getOrElse(ClassSource.MacroQuotes)

              inspectClassDefImpl(cd, _package, InspectMode.AllSources, classSrc)

            case td @ TypeDef(typeDefName: String, _) =>
              log.debug(s"$logPrefix It is TypeDefClause ($typeDefName): ${tree.shortContent}")
              processTypeDef(td, _package)

            case _: Import => // ignore
            case _: ValDef => // ignore

            case _ =>
              logUnexpectedTreeEl(this, tree)
              super.traverseTree(tree)(owner)
        catch
          // There is AssertionError because it can be thrown by scala compiler?!
          //case ex: (Exception | AssertionError) => log.warn(s"$traverseLogPrefix => Unexpected error $ex", ex)
          //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
          case ex: (Exception | AssertionError) => log.error(s"Unexpected error $ex", ex)
    }



    def visitTree(el: Tree): List[FilePackageContainer] =
      var processed: List[FilePackageContainer] = Nil

        val traverser = new TreeTraverser {
        override def traverseTree(tree: Tree)(owner: Symbol): Unit =
          val logPrefix = s"${this.simpleClassName}: "
          try
            tree match
              case p @ PackageClause(pid: Ref, stats: List[Tree]) =>
                log.debug(s"$logPrefix It is PackageClause ($pid): ${tree.shortContent}")
                val packageTreeTraverser = PackageTreeTraverser(p, None)
                packageTreeTraverser.traverseTree(p)(p.symbol)
                processed ::= packageTreeTraverser._package

              case _ =>
                logUnexpectedTreeEl(this, tree)
                super.traverseTree(tree)(owner)
          catch
            // There is AssertionError because it can be thrown by scala compiler?!
            //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
            case ex: (Exception | AssertionError) => log.error(s"$logPrefix Unexpected error $ex", ex)
      }

      traverser.traverseTree(el)(el.symbol)
      processed
    end visitTree




    for tasty <- beanType do
      val tastyPath = tasty.path
      // lets keep it as 'info' log since this info is needed very often
      log.info(s"tasty.path: $tastyPath")
      val tree: Tree = tasty.ast

      if !processedTastyFiles.contains(tastyPath) then
        val packageTags = visitTree(tree)

        val processedClasses = packageTags.flatMap(_.classes.values)
        allProcessedClasses ++= processedClasses

        processedClasses.foreach { cl => classesByFullName.put(cl.fullName, cl) }

        processedTastyFiles.put( tastyPath, processedClasses )
        processedTastyFiles.put( tastyPath.replaceSuffix(".class", ".tasty"), processedClasses )
        processedTastyFiles.put( tastyPath.replaceSuffix(".tasty", ".class"), processedClasses )
    end for

    val allParentRuntimeFullClassNames = allProcessedClasses.flatMap(_.parentTypes).map(_.runtimeTypeName).distinct
    allParentRuntimeFullClassNames
      .filter(className => !classesByFullName.contains(className))
      .foreach { parentClassFullName =>
        // TODO: try to remove loading java class if/when it is possible
        val parentClass = loadClass(parentClassFullName, classLoaders.values)
        val toInspectParent: Boolean = toInspectParentClass(parentClass)

        if (!classesByFullName.contains(parentClassFullName) && toInspectParent)
          inspectClass(parentClass)
      }


    dependenciesJars.foreach { jarPath =>
      if !processedJars.contains(jarPath) then inspectJar(jarPath)
    }

  end inspect

  private def inspectClassDefImpl(using q: Quotes)
    (classDef: q.reflect.ClassDef, _package: FilePackageContainer, inspectMode: InspectMode, classSource: ClassSource): List[_Class] =
    val _classes = processClassDef(classDef, _package, inspectMode, classSource)
      .map(_.copy()(Option(ScalaBeansInspector.this)))
    _package.classes.addAll(_classes.map(cls => (cls.fullName, cls)))
    _classes


  private def inspectJavaClass(_cls: Class[?], scalaBeansInspector: ScalaBeansInspector): _Class =
    import JavaInspectionHelper.*

    val classChain: List[Class[?]] = getAllSubClassesAndInterfaces(_cls)
    val parentTypes = classChain.map(_.getName.nn).map(_Type(_))

    classChain.foreach { c =>
      if toInspectParentClass(c) then
        scalaBeansInspector.inspectClass(c)
    }

    val declaredFields  = _cls.getDeclaredFields.nn.map  { f => val _f = toField(f.nn);   (_f.toKey, _f) }.toMap
    val declaredMethods = _cls.getDeclaredMethods.nn.map { m => val _m = toMethod(m.nn);  (_m.toKey, _m) }.toMap

    val javaClassSimpleName = _cls.getSimpleName.nn
    val scalaClassSimpleName = javaClassSimpleName.afterLastOfAnyCharsOr(".$", javaClassSimpleName)

    val _class: _Class = _Class(
      _cls.getName.nn,
      _cls.getPackageName.nn,
      scalaClassSimpleName,
      ClassKind.Java, Option(ClassSource.of(_cls)),
      parentTypes,
      declaredFields, declaredMethods,
      Option(_cls),
    )(Option(scalaBeansInspector))

    _class
  end inspectJavaClass


end ScalaBeansInspector


class TastyFileNotFoundException protected (message: String, cause: Option[Throwable])
  extends RuntimeException(message, cause.orNull) :
  def this(message: String, cause: Throwable) = this(message, Option(cause))
  def this(message: String) = this(message, None)



class FilePackageContainer (val fullName: String) :
  val classes: mutable.Map[String, _Class] = mutable.HashMap()
  def simpleName: String = fullName.afterLastOr(".", fullName)
  override def toString: String = s"package $fullName \n${ classes.values.mkString("\n") }"
  def withSubPackage(subPackageName: String): FilePackageContainer = FilePackageContainer(s"$fullName.$subPackageName")



def toInspectParentClass(_class: Class[?]): Boolean =
  _class.classKind match
    case ClassKind.Java   => true
    case ClassKind.Scala2 => true
    case ClassKind.Scala3 => getClassLocationUrl(_class).getProtocol != "jar"



extension (obj: AnyRef)
  def simpleClassName: String =
    import scala.language.unsafeNulls
    val cls = obj.getClass
    cls.getSimpleName.ifBlank(cls.getName)
  def shortContent: String =
    val asStr = obj.toString
    val maxLen = 30
    if asStr.length <= maxLen then asStr else asStr.substring(0, maxLen).nn + "..."
