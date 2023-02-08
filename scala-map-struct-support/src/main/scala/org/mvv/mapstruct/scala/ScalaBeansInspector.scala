package org.mvv.mapstruct.scala

import scala.annotation.nowarn
import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.{Inspector, Tasty, TastyInspector}
import ClassKind.classKind

import java.nio.file.Path

val classesToIgnore: Set[_Type] = Set(_Type.ObjectType, _Type("java.lang.Comparable"))


class ScalaBeansInspector extends Inspector :
  import QuotesHelper.*

  private val log = Logger(classOf[ScalaBeansInspector])

  // it contains ONLY 'normal' classes from input tasty file
  private val classesByFullName:  mutable.Map[String, _Class] = mutable.HashMap()
  private val processedTastyFiles: mutable.Map[String, List[_Class]] = mutable.Map()
  private val processedJars: mutable.Set[Path] = mutable.Set()

  def classesDescr: Map[String, _Class] = Map.from(classesByFullName)
  def classDescr(classFullName: String): Option[_Class] = classesByFullName.get(classFullName)

  def inspectClass(fullClassName: String): _Class = inspectClass(fullClassName, Nil*)

  def inspectClass(fullClassName: String, classLoaders: ClassLoader*): _Class =
    inspectClass(loadClass(fullClassName, classLoaders*))

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


  def inspectTastyFile(tastyOrClassFile: String): List[_Class] =
    val tastyFile = if tastyOrClassFile.endsWith(".tasty")
      then tastyOrClassFile
      else tastyOrClassFile.replaceSuffix(".class", ".tasty")
    TastyInspector.inspectTastyFiles(List(tastyFile))(this)
    this.processedTastyFiles.get(tastyFile)
      .map(_.toList) .getOrElse(List())

  def inspectJar(jarPath: Path): List[_Class] =
    processedJars.addOne(jarPath)

    val before = this.classesDescr
    TastyInspector.inspectTastyFilesInJar(jarPath.toString)(this)
    val after = this.classesDescr

    val dif = after.values.toSet -- before.values.toSet
    dif.toList

  override def inspect(using Quotes)(beanType: List[Tasty[quotes.type]]): Unit =
    import quotes.reflect.*

    val dependenciesJars = mutable.Set[Path]()

    for tasty <- beanType do
      log.trace(s"tasty.path: ${tasty.path}")
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

      val _package = _Package(packageClause.toSymbol.get.fullName)
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
      val alreadyProcessed = classesByFullName.get(s"${_package.name}.$typeName")
      if alreadyProcessed.isDefined then return alreadyProcessed.get

      val cls = loadClass(s"${_package.name}.$typeName")
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
            val parentClass = loadClass(parentClassFullName.className)
            val toInspectParent: Boolean = toInspectParentClass(parentClass)

            if (!classesByFullName.contains(parentClassFullName.className) && toInspectParent)
              inspectClass(parentClass)
          }

      case _ =>


    def visitTypeDefEl(_class: _Class, rhs: Tree): Unit = rhs match
      case cd if cd.isClassDef || cd.isTemplate => visitClassEls(_class, getClassMembers(cd))
      case _ =>


    def visitClassEls(_class: _Class, classEls: List[Tree]): Unit =
      val declaredFields = mutable.Map[_FieldKey, _Field]()
      val declaredMethods = mutable.Map[_MethodKey, _Method]()
      val declaredTypeParams = mutable.ArrayBuffer[_TypeParam]()
      classEls.foreach (
        _ match
          case el if el.isDefDef => val m = el.toMethod; declaredMethods.put(m.toKey, m)
          case el if el.isValDef => val f = el.toField;  declaredFields.addOne(f.toKey, f)
          case el if el.isTypeDef => val typeParamName = extractName(el);  declaredTypeParams.addOne(_TypeParam(typeParamName))
          case el => throw IllegalStateException(s"Unexpected class element: [$el].")
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

  def extractName(using Quotes)(el: quotes.reflect.Tree): String =
    el.toSymbol.get.name
    //import quotes.reflect.*
    //el match
    //  case id: Ident => id.name // T O D O: is it safe to use normal pattern matching
    //  case _ => throw IllegalArgumentException(s"Unexpected $el tree element in identifier. ")


  def extractJavaClass(using Quotes)(t: quotes.reflect.Tree): _Type =
    val symbol = t.toSymbol.get
    val clsStr = if symbol.isType
    then symbol.typeRef.dealias.widen.dealias.show // we also can use symbol.fullName
    else symbol.fullName.stripSuffix(".<init>")
    _Type(clsStr) // return for debug


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

    def toSymbol: Option[quotes.reflect.Symbol] =
      // TODO: try to remove risky asInstanceOf[Symbol]
      //if el.symbol.isInstanceOf[Symbol] then Option(el.symbol.asInstanceOf[Symbol]) else None
      Option(el.symbol.asInstanceOf[quotes.reflect.Symbol])

    def isTypeDef: Boolean =
      el.toSymbol.map(s => s.isTypeDef || s.isClassDef).getOrElse(false)

    def isPackageDef: Boolean =
      el.toSymbol .map(_.isPackageDef) .getOrElse(false)

    def isClassDef: Boolean =
      el.toSymbol .map(_.isClassDef) .getOrElse(false)

    def isValDef: Boolean =
      el.toSymbol .map(_.isValDef) .getOrElse(false)

    def isDefDef: Boolean =
      el.toSymbol .map(_.isDefDef) .getOrElse(false)

    //noinspection IsInstanceOf
    // Hacking approach because class Template is really used instead of ClassDef
    // but Template is not present in official APY scala3-library_X.jar!/scala/quoted/Quotes.tasty
    def isTemplate: Boolean =
      el.isInstanceOf[Product] && el.asInstanceOf[Product].productPrefix == "Template"

    def getClassMembers: List[quotes.reflect.Tree] =
      el match
        case cd if cd.isClassDef => cd.asInstanceOf[quotes.reflect.ClassDef].body
        case t if t.isTemplate  => getByReflection(t, "body", "preBody", "unforcedBody")
          .unwrapOption.asInstanceOf[List[quotes.reflect.Tree]]
        case _ => throw IllegalArgumentException(s"Unexpected tree $el.")

    def getClassDefParents: List[quotes.reflect.Tree] =
      el match
        case cd if cd.isClassDef => cd.asInstanceOf[quotes.reflect.ClassDef].parents
        case t if t.isTemplate  => getByReflection(t, "parents", "preParentsOrDerived", "unforcedParents")
          .unwrapOption.asInstanceOf[List[quotes.reflect.Tree]]
        case _ => throw IllegalArgumentException(s"Unexpected tree $el.")

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


    def toMethod(using quotes.reflect.Printer[quotes.reflect.Tree]): _Method =
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

      val methodName: String = m.name

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


def getByReflection(obj: AnyRef, propName: String*): Any =
  val klass = obj.getClass
  propName
    .map( propName =>
      try Option(klass.getMethod(propName).nn.invoke(obj))
      catch case _: Exception =>
          try
            val field: java.lang.reflect.Field = klass.getDeclaredField(propName).nn
            field.setAccessible(true)
            Option(field.get(obj)) // we use/return of 1st successful case
          catch case _: Exception => None
    )
    .find( _.isDefined )
    .getOrElse(throw IllegalArgumentException(s"Property [${klass.getName}.${propName.mkString(", ")}] is not found."))
end getByReflection

extension [T](v: T|Null|Option[T])
  @nowarn @unchecked //noinspection IsInstanceOf
  def unwrapOption: T|Null =
    if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]].orNull else v.asInstanceOf[T|Null]
