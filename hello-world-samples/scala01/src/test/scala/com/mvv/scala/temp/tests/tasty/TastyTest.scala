package com.mvv.scala.temp.tests.tasty

//
import com.mvv.scala.macros.printFields
import org.assertj.core.api.SoftAssertions

import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.*
import scala.quoted.*
import scala.tasty.inspector.*
//
import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}
//
import org.junit.jupiter.api.Test



class _Package (val name: String) :
  val classes: mutable.Map[String, _Class] = mutable.HashMap()
  override def toString: String = s"package $name \n${ classes.values.mkString("\n") }"

private case class _Class (_package: String, simpleName: String) :
  def fullName: String = s"$_package.$simpleName"
  val fields: mutable.ArrayBuffer[_Field] = mutable.ArrayBuffer()
  val methods: mutable.ArrayBuffer[_Method] = mutable.ArrayBuffer()
  override def toString: String = s"Class $fullName, fields: [${fields.mkString(",")}], methods: [${methods.mkString(",")}]"


private enum _Modifier :
  case Static, Getter, Setter

private enum _Visibility :
  case Public, Other


private trait _ClassMember :
  val name: String
  val visibility: _Visibility
  val modifiers: Set[_Modifier]

private case class _Field (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  _type: String,
  )(
  // for debugging only
  val internalValue: Any
  ) extends _ClassMember :
  override def toString: String = s"Field '$name' : $_type (modifiers: $modifiers)"


// it is not used now
private class _Param (
  val name: String,
  val _type: String,
)

private case class _Method (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  resultType: String,
  // scala has to much different kinds of params, for that reason we do not collect them
  hasParams: Boolean,
  )(
  // for debugging only
  val internalValue: Any
  ) extends _ClassMember:
  override def toString: String = s"Method { $name : $resultType, has params: $hasParams }"

private def isListEmpty(list: List[?]) =
  import scala.language.unsafeNulls
  list == null || list.isEmpty

private val _templateArgs = List("constr", "preParentsOrDerived", "self", "preBody")

extension [T](v: T|Null|Option[T])
  @nowarn @unchecked //noinspection IsInstanceOf
  private def unwrapOption: T|Null =
    if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]].orNull else v.asInstanceOf[T|Null]


private def extractJavaClass(using Quotes)(t: quotes.reflect.TypeTree): String =
  t.tpe.widen.dealias.show
  /*
  import quotes.reflect.*

  val asType: Type[?] = tpe.asType
  // TODO: print dif names
  //val asTypeShow = asType.show
  //println(s"asTypeShow: $asTypeShow")

  println(s"tpeDealias.show: ${tpe.widen.dealias.show}")
  println(s"tpe.show: ${tpe.show}")
  println(s"tpeWiden.show: ${tpe.widen.show}")
  println(s"tpeWidenTermRefByName.show: ${tpe.widenTermRefByName.show}")
  println(s"tpeWidenByName.show: ${tpe.widenByName.show}")
  println(s"tpeDealias.show: ${tpe.dealias.show}")
  println(s"tpeClassSymbol.show: ${tpe.classSymbol.map(_.name)}")
  println(s"tpeTypeSymbol.show: ${tpe.typeSymbol.name}")
  println(s"tpeTermSymbol.show: ${tpe.termSymbol.name}")
  */

def fieldModifiers(using Quotes)(field: quotes.reflect.ValDef) = Set[_Modifier]()
def methodModifiers(using Quotes)(method: quotes.reflect.DefDef) = Set[_Modifier]()

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
  def is_Template: Boolean =
    el.isInstanceOf[Product] && el.asInstanceOf[Product].productPrefix == "Template"

  def getClassMembers: List[quotes.reflect.Tree] =
    el match
      case cd if cd.isClassDef => cd.asInstanceOf[quotes.reflect.ClassDef].body
      case t if t.is_Template  => getByReflection(t, "body", "preBody", "unforcedBody")
        .unwrapOption.asInstanceOf[List[quotes.reflect.Tree]]
      case _ => throw IllegalArgumentException(s"Unexpected tree $el.")

  def getParents: List[quotes.reflect.Tree] =
    el match
      case cd if cd.isClassDef => cd.asInstanceOf[quotes.reflect.ClassDef].parents
      case t if t.is_Template  => getByReflection(t, "parents", "preBody", "unforcedBody")
        .unwrapOption.asInstanceOf[List[quotes.reflect.Tree]]
      case _ => throw IllegalArgumentException(s"Unexpected tree $el.")

  //// ???? Dow we need it?
  //def getConstructor: List[Tree] =
  //  el match
  //    case cd if cd.isClassDef => cd.asInstanceOf[ClassDef].constructor
  //    //case t if t.is_Template  => getByReflection(t, "constructor") // template does not have constructor
  //    case _ => throw IllegalArgumentException(s"Unexpected tree $tree.")

  def toField: _Field =
    import quotes.reflect.*
    require(el.isValDef)
    val v = el.asInstanceOf[ValDef]
    _Field(v.name,
      _Visibility.Public, // TODO: impl
      fieldModifiers(v),  // TODO: impl
      extractJavaClass(v.tpt),
    )(v)


  def toMethod(using quotes.reflect.Printer[quotes.reflect.Tree]): _Method =
    import quotes.reflect.*

    require(el.isDefDef)
    val v = el.asInstanceOf[DefDef]

    _Method(v.name,
      _Visibility.Public, // TODO: impl
      methodModifiers(v), // TODO: impl
      extractJavaClass(v.returnTpt),
      !isListEmpty(v.paramss) || !isListEmpty(v.leadingTypeParams)
      || !isListEmpty(v.trailingParamss) || !isListEmpty(v.termParamss))
      (v)

    /*
    val paramss: List[ParamClause] = v.paramss
    printFields("paramss", paramss)

    val leadingTypeParams: List[TypeDef] = v.leadingTypeParams
    printFields("leadingTypeParams", leadingTypeParams)

    val trailingParamss: List[ParamClause] = v.trailingParamss
    printFields("trailingParamss", trailingParamss)

    val trailingParamssAreTheSameAsParams = paramss == trailingParamss
    val trailingParamssAreTheSameAsParams2 = paramss.toString == trailingParamss.toString
    println(s"trailingParamssAreTheSameAsParams: $trailingParamssAreTheSameAsParams, trailingParamssAreTheSameAsParams2: $trailingParamssAreTheSameAsParams2")

    val termParamss: List[TermParamClause] = v.termParamss
    //println(s"termParamss: ${termParamss.show}")
    printFields("termParamss", termParamss)

    val returnTpt: TypeTree = v.returnTpt
    println(s"returnTpt: ${returnTpt.show}")
    printFields("returnTpt", returnTpt)
    */

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


/*
// T O D O: remove it after appearing Template in API
object _Template :
  // constr: DefDef, parents: List[Tree], selfOpt: Option[ValDef], body: List[Statement]
  def unapply(using Quotes)(_template: quotes.reflect.Tree):
      //Option[(constr: quotes.reflect.DefDef, preParentsOrDerived: List[quotes.reflect.Tree],
      //        self: quotes.reflect.ValDef, preBody: List[quotes.reflect.Definition])] =
      (quotes.reflect.DefDef, List[quotes.reflect.Tree],
              quotes.reflect.ValDef, List[quotes.reflect.Definition]) =

    require(is_Template(_template), s"Seems $_template is not Template.")
    val asProduct = _template.asInstanceOf[Product]
    require(asProduct.productElementNames.toList == _templateArgs,
      s"Seems $_template implementation has other arguments.")

    //Option(
      (
      asProduct.productElement(0).asInstanceOf[quotes.reflect.DefDef],
      asProduct.productElement(1).asInstanceOf[List[quotes.reflect.Tree]],
      asProduct.productElement(2).asInstanceOf[quotes.reflect.ValDef],
      asProduct.productElement(3).asInstanceOf[List[quotes.reflect.Definition]],
      )
    //)
  end unapply
*/


def extractName(using Quotes)(el: quotes.reflect.Tree): String =
  el.toSymbol.get.name
  //import quotes.reflect.*
  //el match
  //  case id: Ident => id.name // T O D O: is it safe to use normal pattern matching
  //  case _ => throw IllegalArgumentException(s"Unexpected $el tree element in identifier. ")


class TastyTest :

  @Test
  def inspectBeans(): Unit = {
    val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"
    //val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/CaseScalaClassSample.tasty")

    val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/A3.tasty")
    TastyInspector.inspectTastyFiles(tastyFiles)(ScalaBeansInspector())
  }

  @Test
  def visibilityTest(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"
    //val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/CaseScalaClassSample.tasty")

    val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/AccessVisibilityTestClass.tasty")
    val inspector = ScalaBeansInspector()
    TastyInspector.inspectTastyFiles(tastyFiles)(inspector)

    val a = SoftAssertions()

    val r: Map[String, _Package] = inspector.rawResult
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      .hasSize(1)
      .containsKey("com.mvv.scala.temp.tests.tasty")

    //a.assertThat(r).con

    a.assertAll()
  }

end TastyTest


class ScalaBeansInspector extends Inspector :

  private val context = InspectingContext()
  // it contains ONLY 'normal' classes from input tasty file
  var rawResult: mutable.Map[String, _Package] = mutable.HashMap()

  def inspectClass(tastys: Class[Any])(using Quotes): Unit = {
    ???
  }

  override def inspect(using Quotes)(beanType: List[Tasty[quotes.type]]): Unit =
    import quotes.reflect.*

    for tasty <- beanType do
      println(s"tasty.path: ${tasty.path}")
      val tree = tasty.ast
      println(s"tree: $tree")
      val _classes = visitTree(tree)

      //val aa: java.util.Map[String, _Package] = new java.util.HashMap()
      //aa.computeIfAbsent(_classes.get.name, { k => _Package(k.nn) })

      _classes.foreach { pack =>
          rawResult.getOrElseUpdate(pack.name, _Package(pack.name))
            .classes.addAll(pack.classes) }

      println(s"packages: $_classes")
      //this.rawResult = packages


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
      val _class = _Class(_package.name, typeName)

      println(typeDef.toSymbol.get.name)
      println(typeDef.toSymbol.get.fullName)

      printFields("typeDef", typeDef)
      visitTypeDefEl(_class, rhs)
      _class

    def visitTypeDefEl(_class: _Class, rhs: Tree): Unit = rhs match
      case cd if cd.isClassDef || cd.is_Template => visitClassEls(_class, getClassMembers(cd))
      case _ =>

    def visitClassEls(_class: _Class, classEls: List[Tree]): Unit =
      classEls.foreach { el =>
        el match
          case m if m.isDefDef =>
            _class.methods.addOne(el.toMethod)
          case m if m.isValDef =>
            _class.fields.addOne(el.toField)
      }
  end inspect
end ScalaBeansInspector



class InspectingContext :
  val packages: mutable.Map[String, _Package] = mutable.HashMap()


private class _ScalaBeanProps (val beanType: Any /* TypeRepr[Any] or Type[] ??? */ ) :
  val map: scala.collection.mutable.Map[String, _ScalaProp] = scala.collection.mutable.HashMap()

end _ScalaBeanProps


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
