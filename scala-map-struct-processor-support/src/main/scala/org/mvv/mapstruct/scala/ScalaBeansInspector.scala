package org.mvv.mapstruct.scala

import scala.annotation.nowarn
import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.{Inspector, Tasty, TastyInspector}
//
import ClassKind.classKind
import org.mvv.mapstruct.scala.debug.printFields
import org.mvv.mapstruct.scala.debug.printSymbolInfo
import org.mvv.mapstruct.scala.debug.printTreeSymbolInfo
//
import java.net.{ URI, URL, URLClassLoader }
import java.nio.file.Path


private val classesToIgnore: Set[_Type] = Set(_Type.ObjectType, _Type("java.lang.Comparable"))
private val log: Logger = Logger(classOf[ScalaBeansInspector])


class ScalaBeansInspector extends Inspector :
  import QuotesHelper.*

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
          case "jar" => inspectJar(jarUrlToJarPath(classLocation))
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

  override def inspect(using Quotes)(beanType: List[Tasty[quotes.type]]): Unit =
    import quotes.reflect.*

    val dependenciesJars = mutable.Set[Path]()

    for tasty <- beanType do
      // lets keep it as 'info' since this info is needed very often
      log.info(s"tasty.path: ${tasty.path}")
      val tree: Tree = tasty.ast

      if !processedTastyFiles.contains(tasty.path) then
        val packageTag = visitTree(tree)

        packageTag.foreach { _.classes.foreach { (_, cl) => classesByFullName.put(cl.fullName, cl) } }

        val processedClasses = packageTag.map(_.classes.values.toList) .getOrElse(List[_Class]())
        processedTastyFiles.put( tasty.path, processedClasses )
        processedTastyFiles.put( tasty.path.replaceSuffix(".class", ".tasty"), processedClasses )
        processedTastyFiles.put( tasty.path.replaceSuffix(".tasty", ".class"), processedClasses )

    dependenciesJars.foreach { jarPath =>
      if !processedJars.contains(jarPath) then inspectJar(jarPath)
    }


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
        cls, ClassKind.Scala3, ClassSource.of(cls),
        _package.name, typeName)(this)

      visitParentTypeDefs(_class, rhs)
      visitTypeDefEl(_class, rhs)

      _class.parents.foreach { parentCls =>
        val clsSrc = ClassSource.of(parentCls.runtimeClass)
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

  end inspect

  private def inspectJavaClass(_cls: Class[?], scalaBeansInspector: ScalaBeansInspector): _Class =
    import ReflectionHelper.*

    val _class: _Class = _Class(
      _cls, ClassKind.Java, ClassSource.of(_cls),
      _cls.getPackageName.nn, _cls.getSimpleName.nn)(scalaBeansInspector)

    val classChain: List[Class[?]] = getAllSubClassesAndInterfaces(_cls)

    val parentClassFullNames = classChain.map(_.getName.nn)
    _class.parentTypeNames = parentClassFullNames.map(_Type(_))

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


private class _Package (val name: String) :
  val classes: mutable.Map[String, _Class] = mutable.HashMap()
  override def toString: String = s"package $name \n${ classes.values.mkString("\n") }"


def toInspectParentClass(_class: Class[?]): Boolean =
  _class.classKind match
    case ClassKind.Java   => true
    case ClassKind.Scala2 => true
    case ClassKind.Scala3 => getClassLocationUrl(_class).getProtocol != "jar"


private val _templateArgs = List("constr", "preParentsOrDerived", "self", "preBody")

object QuotesHelper :

  def extractJavaClass(using quotes: Quotes)(_type: quotes.reflect.Tree): _Type =
    _Type(extractClassName(_type))


  def visibility(using Quotes)(el: quotes.reflect.Tree): _Visibility =
    import quotes.reflect.*
    val flags: Flags = el.toSymbol.get.flags
    flags match
      case _ if flags.is(Flags.Private) => _Visibility.Private
      case _ if flags.is(Flags.PrivateLocal) => _Visibility.Private
      case _ if flags.is(Flags.Local) => _Visibility.Other
      case _ if flags.is(Flags.Protected) => _Visibility.Protected
      case _ => _Visibility.Public


  @nowarn("msg=method Static in trait FlagsModule is deprecated")
  private def generalModifiers(using Quotes)(symbol: quotes.reflect.Symbol): Set[_Modifier] =
    import quotes.reflect.*
    val m = mutable.Set[_Modifier]()
    val flags: Flags = symbol.flags

    if flags.is(Flags.FieldAccessor) then m.addOne(_Modifier.ScalaStandardFieldAccessor)
    if flags.is(Flags.ParamAccessor) then m.addOne(_Modifier.ParamAccessor)
    if flags.is(Flags.ExtensionMethod) then m.addOne(_Modifier.ExtensionMethod)
    if flags.is(Flags.Transparent) then m.addOne(_Modifier.Transparent)
    if flags.is(Flags.Macro) then m.addOne(_Modifier.Macro)
    if flags.is(Flags.JavaStatic) || flags.is(Flags.Static)
    then m.addOne(_Modifier.Static)
    Set.from(m)


  // TODO: try to move it (at least some ones) to helper to another file
  extension (using Quotes)(el: quotes.reflect.Tree)

    //// ???? Dow we need it?
    //def getConstructor: List[Tree] =
    //  el match
    //    case cd if cd.isClassDef => cd.asInstanceOf[ClassDef].constructor
    //    //case t if t.isTemplate  => getByReflection(t, "constructor") // template does not have constructor
    //    case _ => throw IllegalArgumentException(s"Unexpected tree $tree.")

    def toField: _Field =
      import quotes.reflect.*
      require(el.isValDef)
      val v = el.asInstanceOf[ValDef]
      val valName = v.name // separate var for debugging
      val asSymbol: Symbol = v.toSymbol.get
      val mod: Set[_Modifier] = generalModifiers(asSymbol)
      val fieldType = extractJavaClass(v.tpt)
      _Field(valName, visibility(v), mod, fieldType)(v)


    def toMethod/*(using quotes.reflect.Printer[quotes.reflect.Tree])*/: _Method =
      import quotes.reflect.*

      require(el.isDefDef)
      val m = el.asInstanceOf[DefDef]

      def isListDeeplyEmpty(paramsOfParams: List[ParamClause]) =
        paramsOfParams.flatMap(_.params).isEmpty

      def paramToType(p: ValDef|TypeDef): _Type =
        val symbol = p.toSymbol.get
        symbol match
          case v if v.isValDef =>
            val asValDef = p.asInstanceOf[ValDef]
            extractJavaClass(asValDef.tpt)
          case t if t.isTypeDef =>
            // T O D O: probably it is not tested
            val asTypeDef = p.asInstanceOf[TypeDef]
            extractJavaClass(asTypeDef)
          case _ => throw IllegalStateException(s"Unexpected param definition [$p].")

      def paramssToTypes(paramss: List[ParamClause]): List[_Type] =
        if paramss.size == 1 && paramss.head.params.size == 0 then Nil // case with non field-accessor but without params
        else paramss.map(_.params.map(paramToType).mkString("|")).map(v => _Type(v))

      try m.name catch case _: Exception =>
        println(s"Bad element $m")
        printTreeSymbolInfo(el)
        printFields("Bad element", el)


      val methodName: String = tryDo( m.name ) .orElse(m.toSymbol.map(_.name)) .get

      val returnType = extractJavaClass(m.returnTpt)

      val paramss = m.paramss // separate var for debug
      val paramTypes: List[_Type] = paramssToTypes(paramss)
      val trailingParamTypes: List[_Type] = paramssToTypes(m.trailingParamss)
      val termParamsTypes: List[_Type] = paramssToTypes(m.termParamss)

      val hasExtraParams =
        (!isListDeeplyEmpty(m.trailingParamss) && trailingParamTypes != paramTypes)
          || (!isListDeeplyEmpty(m.termParamss) && termParamsTypes != paramTypes)
          || m.leadingTypeParams.nonEmpty

      var modifiers: Set[_Modifier] = generalModifiers(m.toSymbol.get)
      if !modifiers.contains(_Modifier.ScalaStandardFieldAccessor) && !hasExtraParams then
        val isCustomGetter = paramss.isEmpty && returnType != _Type.UnitType
        val isCustomSetter = paramTypes.size == 1 && (methodName.endsWith("_=") && methodName.endsWith("_$eq"))
        if isCustomGetter || isCustomSetter then modifiers += _Modifier.ScalaCustomFieldAccessor

      _Method(methodName, visibility(m), Set.from(modifiers), returnType, paramTypes, hasExtraParams)(m)

  end extension
