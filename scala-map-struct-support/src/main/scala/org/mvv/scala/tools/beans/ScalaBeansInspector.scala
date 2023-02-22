package org.mvv.scala.tools.beans

import org.mvv.scala.tools.Logger

import scala.quoted.Quotes
import scala.tasty.inspector.{Inspector, Tasty, TastyInspector}
//
import org.mvv.scala.tools.replaceSuffix
import org.mvv.scala.tools.quotes.toQuotesTypeOf

import scala.annotation.nowarn
import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.{Inspector, Tasty, TastyInspector}
//
import ClassKind.classKind
import org.mvv.scala.mapstruct.debug.{ printFields, printSymbolInfo, printTreeSymbolInfo }
import org.mvv.scala.tools.{ Logger, replaceSuffix, fullName, tryDo, ifBlank, lastAfter }
import org.mvv.scala.tools.beans._Quotes.extractType
//import org.mvv.scala.tools.{ isPackageDef, isClassDef, isTypeDef, isImport, isExprStatement, isValDef }
//import org.mvv.scala.tools.{ isSingletonDef, isApply, isDefDef, isTemplate }
//import org.mvv.scala.tools.{ getClassMembers, getClassDefParents }
//
import java.net.{ URI, URL, URLClassLoader }
import java.nio.file.Path


private val classesToIgnore: Set[_Type] = Set(Types.ObjectType, _Type("java.lang.Comparable"))
private val log: Logger = Logger(classOf[ScalaBeansInspector])


class ScalaBeansInspector extends Inspector :
  //import QuotesHelper.*

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

  private def addClassLoaders(classLoaders: ClassLoader*): Unit =
    classLoaders.foreach { cl =>
      val alreadyAdded = this.classLoaders.values.exists(_ == cl)
      if !alreadyAdded then
        this.classLoaders += (cl.getName.nn, cl)
    }

  private def getJarClassFullNames(jarPath: Path, classCount: Int): List[String] =
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

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
    //import org.mvv.scala.tools.safeSubString as substring
    import org.mvv.scala.tools.safeSubString

    val dependenciesJars = mutable.Set[Path]()

    var allProcessedClasses: List[_Class] = Nil

    def logUnexpectedTreeEl(processor: TreeTraverser|String, treeEl: Tree): Unit =
      val logPrefix: String = processor match
        case s: String => s
        case p: TreeTraverser => p.simpleClassName
      log.warn(s"$logPrefix: Unexpected tree element!" +
        s" Please explicitly process or ignore element: $treeEl")


    def processTypeDef(typeDef: TypeDef, _package: _Package): Unit =
      // TODO: impl
      logUnexpectedTreeEl("processTypeDef", typeDef)


    class PackageTreeTraverser (
      val packageClause: PackageClause,
      val parentPackage: Option[_Package],
      ) extends TreeTraverser {

      //val _package: _Package = parentPackage
      //  .map(parentPack => parentPack.withSubPackage(refName(packageClause.pid)))
      //  .getOrElse(_Package(fullPackageName(refName(packageClause.pid))))
      val _package: _Package = _Package(fullPackageName(packageClause))

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
              log.info(s"$logPrefix It is PackageClause ($pid): ${tree.shortContent}")
              PackageTreeTraverser(p, parentPackage).traverseTree(p)(p.symbol)

            case cd @ ClassDef(classDefName: String, _, _, _, _) =>
              log.info(s"$logPrefix It is ClassDef ($classDefName): ${tree.shortContent}")
              //ClassDefTreeTraverser(cd, _package).traverseTree(cd)(cd.symbol)
              val _class = processClassDef(cd, _package)
              //classesByFullName.put(_class.fullName, _class)
              _package.classes.put(_class.fullName, _class)

            case td @ TypeDef(typeDefName: String, _) =>
              log.info(s"$logPrefix It is TypeDefClause ($typeDefName): ${tree.shortContent}")
              //TypeDefTreeTraverser(td, _package).traverseTree(td)(td.symbol)
              processTypeDef(td, _package)

            case _ =>
              logUnexpectedTreeEl(this, tree)
              super.traverseTree(tree)(owner)
        catch
          // There is AssertionError because it can be thrown by scala compiler?!
          //case ex: (Exception | AssertionError) => log.warn(s"$traverseLogPrefix => Unexpected error $ex", ex)
          //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
          case ex: (Exception | AssertionError) => log.error(s"Unexpected error $ex", ex)
    }

    def processClassDef(classDef: ClassDef, _package: _Package): _Class =

      val _simpleClassName: String = classDef.name
      val _fullClassName: String = s"${_package.fullName}.$_simpleClassName"
      val parents: List[_Type] = classDef.parents
        .map(parentTree => extractType(parentTree))
        .filter(_type => !classesToIgnore.contains(_type))

      //val declaredFields = scala.collection.mutable.ArrayBuffer[_Fields]
      val declaredFields = mutable.ArrayBuffer[_Field]()
      val declaredMethods = mutable.ArrayBuffer[_Method]()

      val body: List[Statement] = classDef.body

      body.foreach { (tree: Tree) =>
        import org.mvv.scala.tools.beans._Quotes.{extractType, toField, toMethod}

        val logPrefix = s"${this.simpleClassName}: "

        try
          tree match
            case PackageClause(pid: Ref, _) => // hm... is it possible??
              log.warn(s"$logPrefix Unexpected package definition [${refName(pid)}] inside class [$_fullClassName].")

            case ClassDef(internalClassName: String, _, _, _, _) =>
              log.info(s"$logPrefix Internal class [$_fullClassName#$internalClassName] is ignored" +
                s" because there is no sense to process such this-dependent classes.")

            case td @ TypeDef(typeName: String, rhs: Tree) =>
              processTypeDef(td, _package)

            case vd @ ValDef(name: String, tpt: TypeTree, rhs: Option[Term]) =>
              val f: _Field = vd.toField
              log.info(s"$logPrefix field: $f")
              declaredFields.addOne(f)

            // functions/methods
            case dd @ DefDef(name: String, paramss: List[ParamClause], tpt: TypeTree, rhs: Option[Term]) =>
              val m: _Method = dd.toMethod
              log.info(s"$logPrefix method: $m")
              declaredMethods.addOne(m)

            case _ =>
              logUnexpectedTreeEl("processClassDef", tree)
        catch
          // There is AssertionError because it can be thrown by scala compiler?!
          //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
          case ex: (Exception | AssertionError) => log.error(s"$logPrefix Unexpected error $ex", ex)
      }

      val runtimeClass: Option[Class[?]] = tryDo(loadClass(_fullClassName))
      val _class = _Class( _package.fullName, _simpleClassName,
        ClassKind.Scala3, runtimeClass.map(cls => ClassSource.of(cls)), runtimeClass,
        ) (this)
      _class.parentTypes = parents
      _class.declaredFields = declaredFields.map(f => (f.toKey, f)).toMap
      _class.declaredMethods = declaredMethods.map(m => (m.toKey, m)).toMap
      _class

    end processClassDef




    def visitTree(el: Tree): List[_Package] =
      var processed: List[_Package] = Nil

      val traverser = new TreeTraverser {
        override def traverseTree(tree: Tree)(owner: Symbol): Unit =
          val logPrefix = s"${this.simpleClassName}: "
          try
            tree match
              case p @ PackageClause(pid: Ref, stats: List[Tree]) =>
                log.info(s"$logPrefix It is PackageClause ($pid): ${tree.shortContent}")
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


  /*
    // TypeDef(name: String, constr: DefDef, parents: List[Tree /* Term | TypeTree */], selfOpt: Option[ValDef], body: List[Statement])
    class TypeDefTreeTraverser(val typeDef: TypeDef, val _package: _Package) extends TreeTraverser {
      val _typeName: String = typeDef.name

      override def traverseTree(tree: Tree)(owner: Symbol): Unit =
        import org.mvv.scala.tools.beans._Quotes.{ extractType, toField, toMethod }
        val logPrefix = s"${this.simpleClassName}: "
        try
          tree match
            case td @ TypeDef(_, _) => if td != this.typeDef then TypeDefTreeTraverser(td, _package)

            case PackageClause(pid: Ref, _) => // hm... is it possible??
              log.warn(s"$logPrefix Unexpected package [${refName(pid)}] definition inside type definition [$_typeName].")

            case cd @ ClassDef(internalClassName: String, _, _, _, _) =>
              ClassDefTreeTraverser(cd, _package)

            case v @ ValDef(_, _, _) =>
              val f: _Field = v.toField
              log.info(s"$logPrefix field: $f")

            // functions/methods
            case defDef @ DefDef(_, _, _, _) =>
              val m: _Method = defDef.toMethod
              log.info(s"$logPrefix method: $m")

            case _ =>
              logUnexpectedTreeEl(this, tree)
              super.traverseTree(tree)(owner)
        catch
          // There is AssertionError because it can be thrown by scala compiler?!
          //case ex: (Exception | AssertionError) => log.warn(s"$traverseLogPrefix => Unexpected error $ex", ex)
          //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
          case ex: (Exception | AssertionError) => log.error(s"Unexpected error $ex", ex)
    }
    */



  /*
    def visitTree(el: Tree): Option[_Package] =
      el match
        case p if p.isPackageDef => Option(visitPackageTag(p.asInstanceOf[PackageClause]))
        case _ => None


    def visitPackageTag(packageClause: PackageClause): _Package =
      val PackageClause(_, children) = packageClause

      val tastyPackageName = packageClause.toSymbol.get.fullName
      val realPackageName = if tastyPackageName == "<empty>" then "" else tastyPackageName
      val _package = _Package(realPackageName)
      children.foreach(
        visitPackageEl(_package, _)
          .foreach(cls => _package.classes.put(cls.simpleName, cls) ))
      _package


    def visitPackageEl(_package: _Package, el: Tree): Option[_Class] = el match
      // usual pattern matching does not work for path-dependent types (starting from scala 2.12)
      case td if td.isTypeDef => Some(visitTypeDef(_package, td.asInstanceOf[TypeDef]))
      case _ => None // currently we need only classes


    def visitTypeDef(_package: _Package, typeDef: TypeDef): _Class =

      val TypeDef(typeName, rhs) = typeDef
      val alreadyProcessed = classesByFullName.get(fullName(_package.name, typeName))
      if alreadyProcessed.isDefined then return alreadyProcessed.get

      //printFields(s"${_package.name}.$typeName  typeDef", typeDef)

      val cls = loadClass(fullName(_package.name, typeName), classLoaders.values)
      val _class = _Class(
        Option(cls), ClassKind.Scala3, ClassSource.of(cls),
        _package.name, typeName)(this)

      visitParentTypeDefs(_class, rhs)
      visitTypeDefEl(_class, rhs)

      _class.parents.foreach { parentCls =>
        val clsSrc = ClassSource.of(parentCls.runtimeClass.get)
        clsSrc match
          case JarClassSource(jarPath) => dependenciesJars.addOne(jarPath)
          case _ =>
      }

      _class


    def visitParentTypeDefs(_class: _Class, rhs: Tree): Unit = rhs match
      case cd if cd.isClassDef || cd.isTemplate =>
        val parents = getClassDefParents(cd).asInstanceOf[List[Tree]]

        val parentFullClassNames = parents
          .map(extractJavaClass(_))
          .filterNot( classesToIgnore.contains(_) )
        _class.parentTypeNames = parentFullClassNames

        parentFullClassNames
          .foreach { parentClassFullName =>
            val parentClass = loadClass(parentClassFullName.className, classLoaders.values)
            val toInspectParent: Boolean = toInspectParentClass(parentClass)

            if (!classesByFullName.contains(parentClassFullName.className) && toInspectParent)
              inspectClass(parentClass)
          }

      case _ =>


    def visitTypeDefEl(_class: _Class, rhs: Tree): Unit = rhs match
      case s if s.isSingletonDef => // nothing important and it has unexpected format
      case cd if cd.isClassDef || cd.isTemplate => visitClassEls(_class, getClassMembers(cd))
      case _ =>


    def visitClassEls(_class: _Class, classEls: List[Tree]): Unit =
      val declaredFields = mutable.Map[_FieldKey, _Field]()
      val declaredMethods = mutable.Map[_MethodKey, _Method]()
      val declaredTypeParams = mutable.ArrayBuffer[_TypeParam]()
      classEls.foreach (
        _ match
          case el if el.isImport => // it is also ValDef... need to skip
          //case el if el.isEnumValueDef => // skipping now
          case el if el.isExprStatement => // skipping now
          case el if el.isValDef => val f = el.toField;  declaredFields.addOne(f.toKey, f)
          case el if el.isApply =>  // some instructions inside class definition
          case el if el.isDefDef => val m = el.toMethod; declaredMethods.put(m.toKey, m)
          case el if el.isTypeDef || el.isTemplate => val typeParamName = extractName(el);  declaredTypeParams.addOne(_TypeParam(typeParamName))
          case el =>
            printFields(s"Unexpected member class", el)
              printTreeSymbolInfo(el)
              log.warn(s"Unexpected member class [$el]. Please add explicit its processing or skipping (similar to <import>).")
        // uncomment to fix warnings
        //throw IllegalStateException(s"Unexpected class element: [$el].") // fir deeper testing
      )
      _class.declaredTypeParams = List.from(declaredTypeParams)
      _class.declaredFields = Map.from(declaredFields)
      _class.declaredMethods = Map.from(declaredMethods)
      */

  end inspect

  private def inspectJavaClass(_cls: Class[?], scalaBeansInspector: ScalaBeansInspector): _Class =
    import ReflectionHelper.*

    val _class: _Class = _Class( _cls.getPackageName.nn, _cls.getSimpleName.nn,
      ClassKind.Java, Option(ClassSource.of(_cls)), Option(_cls),
      )(scalaBeansInspector)

    val classChain: List[Class[?]] = getAllSubClassesAndInterfaces(_cls)

    val parentClassFullNames = classChain.map(_.getName.nn)
    _class.parentTypes = parentClassFullNames.map(_Type(_))

    classChain.foreach { c =>
      if toInspectParentClass(c) then
        scalaBeansInspector.inspectClass(c)
    }

    _class.declaredFields = _cls.getDeclaredFields.nn.map { f =>
      val _f = toField(f.nn);  (_f.toKey, _f)
    }.toMap
    _class.declaredMethods = _cls.getDeclaredMethods.nn.map { m =>
      val _m = toMethod(m.nn);  (_m.toKey, _m)
    }.toMap

    _class
  end inspectJavaClass


end ScalaBeansInspector


class TastyFileNotFoundException protected (message: String, cause: Option[Throwable])
  extends RuntimeException(message, cause.orNull) :
  def this(message: String, cause: Throwable) = this(message, Option(cause))
  def this(message: String) = this(message, None)


// TODO: rename, since it not real package
private class _Package (val fullName: String) :
  val classes: mutable.Map[String, _Class] = mutable.HashMap()
  def simpleName: String = fullName.lastAfter('.').getOrElse(fullName)
  override def toString: String = s"package $fullName \n${ classes.values.mkString("\n") }"
  def withSubPackage(subPackageName: String): _Package = _Package(s"$fullName.$subPackageName")


def toInspectParentClass(_class: Class[?]): Boolean =
  _class.classKind match
    case ClassKind.Java   => true
    case ClassKind.Scala2 => true
    case ClassKind.Scala3 => getClassLocationUrl(_class).getProtocol != "jar"



private def refName(using q: Quotes)(ref: q.reflect.Ref): String =
  import q.reflect.Ident
  ref match
    case Ident(name: String) => name
    case _ =>
      log.warn(s"Ref is not Ident and name is taken by symbol name." +
        s" It may be unexpected behavior which should be better treated in proper non-default way (ref: $ref).")
      ref.symbol.name


private def fullPackageName(using q: Quotes)(packageClause: q.reflect.PackageClause): String =
  val fullPackageName = packageClause.symbol.fullName
  if fullPackageName == "<empty>" then "" else fullPackageName


extension (obj: AnyRef)
  def simpleClassName: String =
    import scala.language.unsafeNulls
    val cls = obj.getClass
    cls.getSimpleName.ifBlank(cls.getName)
  def shortContent: String =
    val asStr = obj.toString
    val maxLen = 30
    if asStr.length <= maxLen then asStr else asStr.substring(0, maxLen).nn + "..."
