package com.mvv.scala.temp.tests.tasty

//
import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.*
//
import java.nio.file.Path
import java.nio.file.Files
import java.lang.reflect.Modifier
import java.lang.reflect.Method as JavaMethod
import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}
//
import com.mvv.scala.macros.printFields


private inline def isNull(v: AnyRef|Null): Boolean =
  import scala.language.unsafeNulls
  val asRaw = v.asInstanceOf[AnyRef]
  asRaw == null

private inline def isNotNull(v: AnyRef|Null): Boolean =
  import scala.language.unsafeNulls
  val asRaw = v.asInstanceOf[AnyRef]
  asRaw != null

extension (s: String)
  def replaceSuffix(oldSuffix: String, newSuffix: String): String =
    if s.endsWith(oldSuffix) then s.stripSuffix(oldSuffix) + newSuffix else s

// utils, move to somewhere
extension [T](v: T|Null)
  //noinspection ScalaUnusedSymbol
  inline def castToNonNullable: T = v.asInstanceOf[T]


private class _Package (val name: String) : // TODO: replace with List or Map
  val classes: mutable.Map[String, _Class] = mutable.HashMap()
  override def toString: String = s"package $name \n${ classes.values.mkString("\n") }"


private def isListEmpty(list: List[?]) =
  import scala.language.unsafeNulls
  list == null || list.isEmpty

private val _templateArgs = List("constr", "preParentsOrDerived", "self", "preBody")

private inline def fileExists(f: String) = Files.exists(Path.of(f))

class TastyFileNotFoundException protected (message: String, cause: Option[Throwable])
  extends RuntimeException(message, cause.orNull) :
  def this(message: String, cause: Throwable) = this(message, Option(cause))
  def this(message: String) = this(message, None)


private def urlToPath(url: String): Option[String] =
  if !url.startsWith("file:") then throw IllegalArgumentException(s"Now only [file:] protocol is supported ($url).")
  val asFile = url.stripPrefix("file:")
  // TODO: improve code by using loop
  { if fileExists(asFile) then return Option(asFile) }
  { val asFileN = asFile.stripPrefix("/");  if fileExists(asFileN) then return Option(asFileN) }
  { val asFileN = asFile.stripPrefix("//"); if fileExists(asFileN) then return Option(asFileN) }
  //throw TastyFileNotFoundException(s"$asFile is not found.")
  None

extension [T](v: T|Null|Option[T])
  @nowarn @unchecked //noinspection IsInstanceOf
  private def unwrapOption: T|Null =
    if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]].orNull else v.asInstanceOf[T|Null]

// TODO: in any case add support of processing pure java classes if no tasty file.
val classesToIgnore: Set[String] = Set("java.lang.Object", "java.lang.Comparable")

//private def extractJavaClass(using Quotes)(t: quotes.reflect.TypeTree): String = t.tpe.widen.dealias.show
private def extractJavaClass(using Quotes)(t: quotes.reflect.Tree): String =
  val symbol = t.toSymbol.get
  if symbol.isType
    then symbol.typeRef.dealias.widen.dealias.show // we also can use symbol.fullName
    else symbol.fullName.stripSuffix(".<init>")

def fieldModifiers(using Quotes)(field: quotes.reflect.ValDef): Set[_Modifier] =
  field.toSymbol .map(generalModifiers(_)) .getOrElse(Set[_Modifier]())
def methodModifiers(using Quotes)(method: quotes.reflect.DefDef): Set[_Modifier] =
  method.toSymbol .map(generalModifiers(_)) .getOrElse(Set())


@nowarn("msg=method Static in trait FlagsModule is deprecated")
def generalModifiers(using Quotes)(symbol: quotes.reflect.Symbol): Set[_Modifier] =
  import quotes.reflect.*
  val m = mutable.Set[_Modifier]()
  val flags: Flags = symbol.flags

  if flags.is(Flags.FieldAccessor)   then m.addOne(_Modifier.FieldAccessor)
  if flags.is(Flags.ParamAccessor)   then m.addOne(_Modifier.ParamAccessor)
  if flags.is(Flags.ExtensionMethod) then m.addOne(_Modifier.ExtensionMethod)
  if flags.is(Flags.Transparent)     then m.addOne(_Modifier.Transparent)
  if flags.is(Flags.Macro)           then m.addOne(_Modifier.Macro)
  if flags.is(Flags.JavaStatic) || flags.is(Flags.Static)
                                     then m.addOne(_Modifier.Static)
  Set.from(m)

def visibility(using Quotes)(el: quotes.reflect.Tree): _Visibility =
  import quotes.reflect.*
  val flags: Flags = el.toSymbol.get.flags
  if flags.is(Flags.Private) || flags.is(Flags.PrivateLocal)
    || flags.is(Flags.Local) || flags.is(Flags.Protected)
    then _Visibility.Public
    else _Visibility.Other

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
    _Field(v.name,
      visibility(v),
      fieldModifiers(v),
      extractJavaClass(v.tpt),
    )(v)


  def toMethod(using quotes.reflect.Printer[quotes.reflect.Tree]): _Method =
    import quotes.reflect.*

    require(el.isDefDef)
    val m = el.asInstanceOf[DefDef]

    if (m.name == "protectedMethod2") {
      println(m)
    }

    def isListDeeplyEmpty(paramsOfParams: List[ParamClause]) =
      paramsOfParams.flatMap(_.params).isEmpty

    val paramss: List[ParamClause] = m.paramss
    val leadingTypeParams: List[TypeDef] = m.leadingTypeParams
    val trailingParamss: List[ParamClause] = m.trailingParamss
    val termParamss: List[TermParamClause] = m.termParamss

    val hasParams = !isListDeeplyEmpty(m.paramss) || !isListEmpty(m.leadingTypeParams)
      || !isListDeeplyEmpty(m.trailingParamss) || !isListDeeplyEmpty(m.termParamss)

    _Method(m.name,
      visibility(m),
      methodModifiers(m),
      extractJavaClass(m.returnTpt),
      hasParams)
      (m)

end extension


/*
def paramClauseToString(using Quotes)(p: quotes.reflect.ParamClause) = {
  p.params.map { pp =>
    pp match
      case pp.isValDef  => val ppv = pp.asInstanceOf[ValDef];  s"val-param,  ${typeTreeToString(ppv.tpt)}"
      case pp.isTypeDef => val ppt = pp.asInstanceOf[TypeDef]; s"type-param, ${typeTreeToString(ppt.tpt)}"
  }
}

def typeTreeToString(using Quotes)(tt: quotes.reflect.TypeTree) = {
  p.params.map { pp =>
    pp match
      case pp.isValDef => val ppv = pp.asInstanceOf[ValDef]; s"val-param, ${typeTreeToString(ppv.tpt)}"
      case pp.isTypeDef =>
  }
}
*/


def getByReflection(obj: AnyRef, propNames: String*): Any =
  val klass = obj.getClass
  propNames
    .map( propName =>
      try
        Option(klass.getMethod(propName).nn.invoke(obj))
      catch
        case _: Exception =>
          try
            val field: java.lang.reflect.Field = klass.getDeclaredField(propName).nn
            field.setAccessible(true)
            Option(field.get(obj)) // we use/return 1st successful case
          catch case _: Exception => None
      )
    .find( _.isDefined )
    .getOrElse(throw IllegalArgumentException(s"Property [${klass.getName}.${propNames.mkString(", ")}] is not found."))
end getByReflection


def extractName(using Quotes)(el: quotes.reflect.Tree): String =
  el.toSymbol.get.name
  //import quotes.reflect.*
  //el match
  //  case id: Ident => id.name // T O D O: is it safe to use normal pattern matching
  //  case _ => throw IllegalArgumentException(s"Unexpected $el tree element in identifier. ")

enum PropertyOwnerKindType :
  case Java, Scala


/**
 * It probably has to much info, but it is to implement easily both MapStruct extension and java bean descriptor.
 */
case class Property (
  name: String,
  propertyType: Class[?],
  ownerKind: PropertyOwnerKindType,
  ownerClass: Class[?],
  javaGetMethods: List[JavaMethod],
  javaSetMethods: List[JavaMethod],
  owner: _Class,
)

extension (_class: _Class)
  def toBeanProperties: Map[String, Property] = {
    //val classChain = _class.parents.re
    ???
  }

class ScalaBeansInspector extends Inspector :

  private val javaBeansInspector = JavaBeansInspector()

  // it contains ONLY 'normal' classes from input tasty file
  private val classesByFullName:  mutable.Map[String, _Class] = mutable.HashMap()
  private val processedTastyFiles: mutable.Map[String, List[_Class]] = mutable.Map()

  //def classesDescr: Map[String, _Class] = Map.from(classesByFullName)
  def classesDescr: Map[String, _Class] =
    // TODO: is it good approach?? A bit expensive...
    val b = Map.newBuilder[String, _Class]
    b ++= classesByFullName
    b ++= javaBeansInspector.classesDescr
    b.result()
  def classDescr(classFullName: String): Option[_Class] = classesByFullName.get(classFullName)

  private def inspectByClassName(fullClassName: String): _Class =
    // TODO: try to use different kind of class-loaders
    val classPath: Option[String] = findClassPathUrl(fullClassName)
    classPath
      //.map ( path => urlToString (path.stripSuffix(".class") + ".tasty") )
      //.map ( (path: String) => urlToString (path.stripSuffix(".class") + ".tasty") )
      .flatMap ( url => urlToPath (url.stripSuffix(".class") + ".tasty") )
      //.flatMap ( inspectTastyFile )
      .map { (path: String) => inspectTastyFile(path).ensuring(_.nonEmpty, s"Result of tasty is empty [$path].") .head }
      .getOrElse ( javaBeansInspector.inspect(fullClassName) )

  private def findClassPathUrl(fullClassName: String): Option[String] =
    val cls = Class.forName(fullClassName)
    val asResource = s"${cls.nn.getSimpleName}.class"
    val thisClassUrl = cls.nn.getResource(asResource)
    //require(thisClassUrl != null, s"$asResource is not found.")
    //val tastyUrl = thisClassUrl.nn.toString.nn.stripSuffix(".class").nn.concat(".tasty").nn
    //tastyUrl
    if thisClassUrl == null then None else Option(thisClassUrl.nn.toString)

    //val packageSubPath = cls.getPackageName.replace('.', '/')
    //val dirUrl = thisClassUrl.toString.stripSuffix(s"$packageSubPath/$fullClassName.class")


  def inspectTastyFile(tastyFile: String): List[_Class] =
    TastyInspector.inspectTastyFiles(List(tastyFile))(this)
    this.processedTastyFiles.get(tastyFile)
      .map(_.toList) .getOrElse(List())

  override def inspect(using Quotes)(beanType: List[Tasty[quotes.type]]): Unit =
    import quotes.reflect.*

    for tasty <- beanType do
      println(s"tasty.path: ${tasty.path}")
      val tree: Tree = tasty.ast
      println(s"tree: $tree")

      if !processedTastyFiles.contains(tasty.path) then
        val packageTag = visitTree(tree)

        packageTag.foreach { _.classes.foreach { (_, cl) => classesByFullName.put(cl.fullName, cl) } }

        println(s"packages: $packageTag")
        val processedClasses = packageTag.map(_.classes.values.toList) .getOrElse(List[_Class]())
        processedTastyFiles.put( tasty.path, processedClasses )
        processedTastyFiles.put( tasty.path.replaceSuffix(".class", ".tasty"), processedClasses )
        processedTastyFiles.put( tasty.path.replaceSuffix(".tasty", ".class"), processedClasses )


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
      val alreadyProcessed = classesByFullName.get(_Class.fullName(_package.name, typeName))
      if alreadyProcessed.isDefined then return alreadyProcessed.get

      val _class = _Class(_package.name, typeName)

      visitParentTypeDefs(_class, rhs)
      visitTypeDefEl(_class, rhs)
      mergeAllDeclaredMembers(_class)
      _class


    def visitParentTypeDefs(_class: _Class, rhs: Tree): Unit = rhs match
      case cd if cd.isClassDef || cd.isTemplate =>
        val parents = getClassDefParents(cd).asInstanceOf[List[Tree]]
        println(s"parents: $parents")

        parents
          .map(extractJavaClass(_))
          .filterNot( classesToIgnore.contains(_) )
          .foreach { parentClassFullName =>
            println(parentClassFullName)

            if (!classesByFullName.contains(parentClassFullName))
              inspectByClassName(parentClassFullName)

            val processedParent: Option[_Class] = classesByFullName.get(parentClassFullName)
              .orElse( javaBeansInspector.classDescr(parentClassFullName) )
            processedParent
              .orElse(throw IllegalStateException(s"Class [$parentClassFullName] is not found/processed."))
              .foreach(_class.parents.addOne)
          }

      case _ =>


    def visitTypeDefEl(_class: _Class, rhs: Tree): Unit = rhs match
      case cd if cd.isClassDef || cd.isTemplate => visitClassEls(_class, getClassMembers(cd))
      case _ =>


    def visitClassEls(_class: _Class, classEls: List[Tree]): Unit =
      classEls.foreach (
        _ match
          case el if el.isDefDef =>
            val m = el.toMethod; _class.declaredMethods.put(m.toKey, m)
          case el if el.isValDef =>
            val f = el.toField;  _class.declaredFields.addOne(f.name, f)
      )

  end inspect
end ScalaBeansInspector


private class _BeanProps (val beanType: Any /* TypeRepr[Any] or Type[] ??? */ ) :
  val map: scala.collection.mutable.Map[String, _Prop] = scala.collection.mutable.HashMap()


private class _Prop (val name: String) :
  var propType: Any = uninitialized // TypeRepr[Any] or Type[] ???
  var getter: Option[Any] = None
  var setter: Option[Any] = None


class ScalaBeanProps (val asJavaClass: Class[Any]) extends java.beans.BeanInfo :
  override def getBeanDescriptor: BeanDescriptor = BeanDescriptor(asJavaClass)

  override def getEventSetDescriptors: Array[EventSetDescriptor] = Array[EventSetDescriptor]()

  override def getDefaultEventIndex: Int = -1

  override def getPropertyDescriptors: Array[PropertyDescriptor] = ???

  override def getDefaultPropertyIndex: Int = -1

  override def getMethodDescriptors: Array[MethodDescriptor] = Array()

  override def getAdditionalBeanInfo: Array[BeanInfo] = Array[BeanInfo]()

  override def getIcon(iconKind: Int): Image|Null = null
end ScalaBeanProps


class JavaBeansInspector :
  private val classesByFullName: mutable.Map[String, _Class] = mutable.HashMap()

  def classesDescr: Map[String, _Class] = classesByFullName.toMap
  def classDescr(classFullName: String): Option[_Class] = classesByFullName.get(classFullName)

  def inspect(klass: Class[?]): _Class = inspect(klass.getName.nn)

  def inspect(fullClassName: String): _Class =

    val alreadyProcessedClass = classesByFullName.get(fullClassName)
    if alreadyProcessedClass.isDefined then return alreadyProcessedClass.get

    val _cls = Class.forName(fullClassName).nn
    val _class = _Class(_cls.getPackageName.nn, _cls.getSimpleName.nn)

    val classChain: List[Class[?]] = getAllSubClassesAndInterfaces(_cls)
    //classChain.reverse.foreach { c =>
    classChain.foreach { c =>
      _class.parents.addOne(inspect(c.getName.nn))
    }

    _cls.getDeclaredFields.nn.foreach { f =>
      val _f = toField(f.nn); _class.declaredFields.put(_f.name, _f) }
    _cls.getDeclaredMethods.nn.foreach { m =>
      val _m = toMethod(m.nn); _class.declaredMethods.put(_m.toKey, _m) }

    mergeAllDeclaredMembers(_class)
    classesByFullName.put(_class.fullName, _class)

    _class
  end inspect

end JavaBeansInspector


private def visibilityFromModifiers(modifiers: Int): _Visibility =
  import java.lang.reflect.Modifier
  modifiers match
    case mod if Modifier.isPublic(mod) => _Visibility.Public
    //case mod if Modifier.isPrivate(mod)   => _Visibility.Other
    //case mod if Modifier.isProtected(mod) => _Visibility.Other
    case _ => _Visibility.Other

def visibilityOf(f: java.lang.reflect.Field): _Visibility = visibilityFromModifiers(f.getModifiers)
def visibilityOf(m: JavaMethod): _Visibility = visibilityFromModifiers(m.getModifiers)


private def getClassesAndInterfacesImpl(
    cls: Class[?], interfaces: Array[Class[?]]): List[Class[?]] =
  val all = mutable.ArrayBuffer[Class[?]]()
  import scala.language.unsafeNulls
  var c: Class[?]|Null = cls
  while c != null && c != classOf[Object] && c != classOf[Any] && c != classOf[AnyRef] do
    all.addOne(cls)
    c = cls.getSuperclass.nn

  interfaces.nn.foreach { i => all.addOne(i.nn) }
  all.distinct.toList

def getAllSubClassesAndInterfaces(cls: Class[?]): List[Class[?]] =
  import scala.language.unsafeNulls
  getClassesAndInterfacesImpl(cls.getSuperclass, cls.getInterfaces): List[Class[?]]


extension (m: JavaMethod)
  def toMethod: _Method =
    _Method(m.getName.nn, visibilityOf(m), methodModifiers(m), m.getReturnType.nn.getName.nn, m.getParameterCount != 0)(m)
extension (f: java.lang.reflect.Field)
  def toField: _Field =
    _Field(f.getName.nn, visibilityOf(f), fieldModifiers(f), f.getType.nn.getName.nn)(f)


def generalModifiers(member: java.lang.reflect.Member): Set[_Modifier] =
  import java.lang.reflect.Modifier
  member.getModifiers match
    case m if Modifier.isStatic(m) => Set(_Modifier.Static)
    case _ => Set()

def fieldModifiers(field: java.lang.reflect.Field): Set[_Modifier] =
  generalModifiers(field)

def methodModifiers(m: JavaMethod): Set[_Modifier] =
  //val mod: MutableSet[_Modifier] = scala.collection.mutable.Set.from(generalModifiers(m))
  val mod: mutable.Set[_Modifier] = mutable.Set.from(generalModifiers(m))
  val mName = m.getName.nn
  val paramCount = m.getParameterCount
  val returnType = m.getReturnType

  val isGetAccessor = mName.startsWith("get") && mName.length > 3
                   && paramCount == 0
                   && returnType != Void.TYPE && returnType != classOf[Unit]

  val isIsAccessor = mName.startsWith("is") && mName.length > 2 && paramCount == 0
                   && (returnType == Boolean || returnType == classOf[Boolean]
                      || returnType == java.lang.Boolean.TYPE || returnType == classOf[java.lang.Boolean])
  val isSetAccessor = mName.startsWith("set") && mName.length> 3 && paramCount == 1
                   && (returnType == Void.TYPE && returnType == classOf[Unit])

  if isGetAccessor || isIsAccessor || isSetAccessor then mod.add(_Modifier.FieldAccessor)

  mod.toSet
end methodModifiers

