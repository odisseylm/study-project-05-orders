package com.mvv.scala.temp.tests.tasty

//
import com.mvv.scala.macros.printFields

import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.*
import scala.quoted.*
import scala.tasty.inspector.*
//
import java.nio.file.Path
import java.nio.file.Files
//
import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}


private def isListEmpty(list: List[?]) =
  import scala.language.unsafeNulls
  list == null || list.isEmpty

private val _templateArgs = List("constr", "preParentsOrDerived", "self", "preBody")

private inline def fileExists(f: String) = Files.exists(Path.of(f))

private def urlToString(url: String): String =
  if !url.startsWith("file:") then throw IllegalArgumentException(s"Now only [file:] protocol is supported ($url).")
  val asFile = url.stripPrefix("file:")
  if fileExists(asFile) then { return asFile }
  { val asFileN = asFile.stripPrefix("/");  if fileExists(asFileN) then return asFileN }
  { val asFileN = asFile.stripPrefix("//"); if fileExists(asFileN) then return asFileN }
  throw IllegalArgumentException(s"$asFile is not found.")

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

    _Method(m.name,
      visibility(m),
      methodModifiers(m),
      extractJavaClass(m.returnTpt),
      !isListEmpty(m.paramss) || !isListEmpty(m.leadingTypeParams)
      || !isListEmpty(m.trailingParamss) || !isListEmpty(m.termParamss))
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



class ScalaBeansInspector extends Inspector :

  private val context = InspectingContext()
  // it contains ONLY 'normal' classes from input tasty file
  val classesByFullName:  mutable.Map[String, _Class] = mutable.HashMap()
  private val processedTastyFiles: mutable.ArrayBuffer[String] = mutable.ArrayBuffer()

  //val tastyPath: String = classFullnameToTastyFile(p)
  //inspect(tastyPath)
  private def inspectByClassName(fullClassName: String): Unit =
    // TODO: try to use different kind of class-loaders
    val classPath: String = findClassPathUrl(fullClassName)
    val tastyPath = classPath.stripSuffix(".class") + ".tasty"
    inspectTastyFile(urlToString(tastyPath))

  private def findClassPathUrl(fullClassName: String): String =
    val cls = Class.forName(fullClassName)
    val asResource = s"${cls.nn.getSimpleName}.class"
    val thisClassUrl = cls.nn.getResource(asResource)
    require(thisClassUrl != null, s"$asResource is not found.")
    //val tastyUrl = thisClassUrl.nn.toString.nn.stripSuffix(".class").nn.concat(".tasty").nn
    //tastyUrl
    thisClassUrl.nn.toString

    //val packageSubPath = cls.getPackageName.replace('.', '/')
    //val dirUrl = thisClassUrl.toString.stripSuffix(s"$packageSubPath/$fullClassName.class")


  def inspectTastyFile(tastyFile: String): Unit =
    TastyInspector.inspectTastyFiles(List(tastyFile))(this)

  override def inspect(using Quotes)(beanType: List[Tasty[quotes.type]]): Unit =
    import quotes.reflect.*

    for tasty <- beanType do
      println(s"tasty.path: ${tasty.path}")
      val tree: Tree = tasty.ast
      println(s"tree: $tree")

      if !processedTastyFiles.contains(tasty.path) then
        val _classes = visitTree(tree)

        _classes.foreach { _.classes.foreach { (_, cl) => classesByFullName.put(cl.fullName, cl) } }
        //_classes.foreach { _.classes.foreach { cl => classesByFullName.put(cl._1, cl._2) } }
        //_classes.foreach { _.classes.foreach { cl => classesByFullName(cl) } }

        println(s"packages: $_classes")
        processedTastyFiles.addOne(tasty.path)


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
            processedParent
              .orElse(throw IllegalStateException(s"Class [$parentClassFullName] is not found/processed."))
              .foreach(_class.parents.addOne)
          }

      case _ =>

    def visitTypeDefEl(_class: _Class, rhs: Tree): Unit = rhs match
      case cd if cd.isClassDef || cd.isTemplate => visitClassEls(_class, getClassMembers(cd))
      case _ =>

    def visitClassEls(_class: _Class, classEls: List[Tree]): Unit =
      classEls.foreach { el =>
        el match
          case m if m.isDefDef =>
            val mm = el.toMethod
            _class.declaredMethods.put(mm.toKey, mm)
          case m if m.isValDef =>
            val ff = el.toField
            _class.declaredFields.addOne(ff.name, ff)
      }
  end inspect
end ScalaBeansInspector


class InspectingContext :
  val packages: mutable.Map[String, _Package] = mutable.HashMap()


private class _ScalaBeanProps (val beanType: Any /* TypeRepr[Any] or Type[] ??? */ ) :
  val map: scala.collection.mutable.Map[String, _ScalaProp] = scala.collection.mutable.HashMap()


private class _ScalaProp (val name: String) :
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
