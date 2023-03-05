package org.mvv.scala.tools.inspection.light

import scala.collection.mutable
import scala.collection.concurrent
import scala.quoted.Quotes
import scala.tasty.inspector.{ Inspector, Tasty, TastyInspector }
//
import org.mvv.scala.tools.{ isOneOf, afterLastOrOrigin }
import org.mvv.scala.tools.quotes.{ classSymbolDetails, classExists, getFullClassName, classFullPackageName }
import org.mvv.scala.tools.quotes.{ asClassDef, asDefDef, asValDef }
import org.mvv.scala.tools.inspection.{ _Class, _Type }
import org.mvv.scala.tools.inspection._Quotes.{ toMethod, toField, classKind, extractTreeType }



//noinspection ScalaFileName
class ScalaBeanInspector :
  private val classesByFullName: concurrent.Map[String, _Class] = concurrent.TrieMap()

  private class InspectorImpl (val classFullName: String) extends Inspector :
    override def inspect(using q: Quotes)(beanTypes: List[Tasty[q.type]]): Unit =
      import q.reflect.*

      if !classExists(classFullName) then
        throw ClassNotFoundException(s"Class [$classFullName] is not found.")

      val classSymbol = Symbol.classSymbol(classFullName)
      if !classSymbol.isClassDef then
        throw ClassNotFoundException(s"Class [$classFullName] is not found.")

      val _class = processClassDef(classSymbol.tree.asClassDef)
      classesByFullName.put(_class.fullName, _class)


  def classesDescr: Map[String, _Class] = Map.from(classesByFullName) // according to doc it is thread-safe
  def classDescr(classFullName: String): Option[_Class] = classesByFullName.get(classFullName)


  private var toInspect: String = ""

  def inspectClass(cls: Class[?]): _Class =
    inspectClass(cls.getName.nn)

  def inspectClass(fullClassName: String): _Class =
    toInspect = fullClassName

    val tmpTastyFile =
      "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala3-samples/target/classes/com/mvv/scala3/samples/Trait1.tasty"
      //"/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala3-samples/target/classes/com/mvv/scala3/samples/Scala3ClassWithMethods.tasty"

    val invoker = InspectorImpl(fullClassName)
    TastyInspector.inspectTastyFiles(List(tmpTastyFile))(invoker)
    classesByFullName.getOrElse(fullClassName,
      throw ClassNotFoundException(s"Class [$fullClassName] is not found."))


  /*
  override def inspect(using q: Quotes)(beanTypes: List[Tasty[q.type]]): Unit =
    import q.reflect.*

    val bean = beanTypes.head
    println(s"%%% ${bean.path}\nAST: ${bean.ast}")

    val clsS = Symbol.classSymbol(toInspect)
    println(s"%%% 4567 ${classSymbolDetails(toInspect)}")

    // valMethod986
    val methodMembers: List[Symbol] = clsS.methodMembers
    methodMembers.filter(_.name.isOneOf("valMethod986", "method987", "methodWithMatch1"))
      .foreach { ms =>
        val mTree = ms.tree
        println(s"%%% method $mTree")
      }

  //def inspectTastyFile(tastyOrClassFile: String): List[_Class] =
  //  TastyInspector.inspectTastyFiles(List("/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala3-samples/target/classes/com/mvv/scala3/samples/Trait1.tasty"))(this)
  */

  private def processClassDef(using q: Quotes)(classDef: q.reflect.ClassDef): _Class =
    import q.reflect.*
    import org.mvv.scala.tools.inspection._Quotes.toMethod

    println(s"%%% classDef: $classDef")

    val classDefParents: List[Tree /* Term | TypeTree */] = classDef.parents
    val parentTypes: List[_Type] = classDefParents.map(p => extractTreeType(p))

    val clsSymbol: Symbol = classDef.symbol

    val declaredMethodsSymbols: List[Symbol] = clsSymbol.declaredMethods
    val methodMembersSymbols: List[Symbol] = clsSymbol.methodMembers
    val declaredFieldsSymbols: List[Symbol] = clsSymbol.declaredFields
    val fieldMembersSymbols: List[Symbol] = clsSymbol.fieldMembers

    val declaredMethods = declaredMethodsSymbols.map { m => m.tree.asDefDef.toMethod }
    val allMethods = methodMembersSymbols.map { m => m.tree.asDefDef.toMethod }

    val declaredFields = declaredFieldsSymbols.map { m => m.tree.asValDef.toField }
    val allFields = fieldMembersSymbols.map { m => m.tree.asValDef.toField }

    val fullClassName = getFullClassName(classDef)
    val _package = classFullPackageName(classDef)
    val simpleName = fullClassName.afterLastOrOrigin(".")
    val classKind = classDef.classKind

    //val classChain: List[Class[?]] = getAllSubClassesAndInterfaces(_cls)
    //val parentTypes = classChain.map(_.getName.nn).map(_Type(_))
    //val parentTypes = Nil

    val _class = _Class(
      fullClassName, _package, simpleName,
      classKind, parentTypes,
      allFields.map(f => (f.toKey, f)).toMap,
      allMethods.map(f => (f.toKey, f)).toMap,
      declaredFields.map(f => (f.toKey, f)).toMap,
      declaredMethods.map(f => (f.toKey, f)).toMap,
      None
    )

    _class