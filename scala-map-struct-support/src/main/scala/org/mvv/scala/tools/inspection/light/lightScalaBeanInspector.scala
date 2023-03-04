package org.mvv.scala.tools.inspection.light

import scala.quoted.Quotes
import scala.tasty.inspector.{ Inspector, Tasty, TastyInspector }
//
import org.mvv.scala.tools.isOneOf
import org.mvv.scala.tools.quotes.classSymbolDetails
import org.mvv.scala.tools.inspection.tasty._Class


val temp = 22



class LightScalaBeanInspector extends Inspector :

  def classesDescr: Map[String, _Class] = ???
  def classDescr(classFullName: String): Option[_Class] = ???


  private var toInspect: String = ""
  def inspectClass(fullClassName: String): Unit =
    toInspect = fullClassName
    val tmpTastyFile =
      //"/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala3-samples/target/classes/com/mvv/scala3/samples/Trait1.tasty"
      "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala3-samples/target/classes/com/mvv/scala3/samples/Scala3ClassWithMethods.tasty"

    TastyInspector.inspectTastyFiles(List(tmpTastyFile))(this)


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
