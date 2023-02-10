package org.mvv.mapstruct.scala.debug

private val log = org.mvv.mapstruct.scala.Logger("org.mvv.mapstruct.scala.debug")

//noinspection ScalaUnusedSymbol
// Debug functions
// temp
def printFields(label: String, obj: Any): Unit =
  log.info(s"\n\n$label   $obj")
  import scala.language.unsafeNulls
  //noinspection TypeCheckCanBeMatch // scala3 warns about non matchable type
  if obj .isInstanceOf [List[?]] then
    obj.asInstanceOf[List[Any]].zipWithIndex
      .foreach { case (el, i) => printFields(s"$label $i:", _) }
  else
    allMethods(obj).foreach( printField(obj.getClass.getSimpleName, obj, _) )


def printField(label: String, obj: Any, prop: String): Unit =
  try { log.info(s"$label.$prop: ${ getProp(obj, prop) }") } catch { case _: Exception => }


//noinspection ScalaUnusedSymbol
def allMethods(obj: Any): List[String] =
  import scala.language.unsafeNulls
  obj.getClass.getMethods .map(_.getName) .toList


//noinspection ScalaUnusedSymbol
def allMethodsDetail(obj: Any): String =
  import scala.language.unsafeNulls
  obj.getClass.getMethods /*.map(_.getName)*/ .toList .mkString("\n")


def getProp(obj: Any, method: String): Any = {
  import scala.language.unsafeNulls
  val methodMethod = try { obj.getClass.getDeclaredMethod(method) } catch { case _: Exception => obj.getClass.getMethod(method) }
  val v = methodMethod.invoke(obj)
  //noinspection TypeCheckCanBeMatch
  if (v.isInstanceOf[Iterator[Any]]) {
    v.asInstanceOf[Iterator[Any]].toList
  } else v
}
