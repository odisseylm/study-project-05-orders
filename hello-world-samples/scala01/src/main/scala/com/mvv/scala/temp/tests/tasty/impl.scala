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
import java.net.URL
import java.lang.reflect.Modifier
import java.lang.reflect.Method as JavaMethod
import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}
//
import com.mvv.scala.macros.printFields









//private def urlToPath(url: String): Option[String] = url match
//  case fileUrl if fileUrl.startsWith("file:") => fileUrlToPath(url)
//  // TODO: temp
//  case jarUrl if jarUrl.startsWith("jar:file:") =>
//    val aaa = url.stripPrefix("jar:file:")
//    Option(aaa.substring(0, aaa.indexOf(".jar!") + 4).nn)
//  //case jarUrl if jarUrl.startsWith("jar:file:") => fileUrlToPath(url.stripPrefix("jar:"))
//  case _ => None
//
//private def fileUrlToPath(url: String): Option[String] =
//  require(url.startsWith("file:"), s"Now only [file:] protocol is supported ($url).")
//  val asFile = url.stripPrefix("file:")
//  // TODO: improve code by using loop
//  { if fileExists(asFile) then return Option(asFile) }
//  { val asFileN = asFile.stripPrefix("/");  if fileExists(asFileN) then return Option(asFileN) }
//  { val asFileN = asFile.stripPrefix("//"); if fileExists(asFileN) then return Option(asFileN) }
//  //throw TastyFileNotFoundException(s"$asFile is not found.")
//  None




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





extension (_class: _Class)
  def toBeanProperties: Map[String, Property] = {
    //val classChain = _class.parents.re
    ???
  }



private class _BeanProps (val beanType: Any /* TypeRepr[Any] or Type[] ??? */ ) :
  val map: scala.collection.mutable.Map[String, _Prop] = scala.collection.mutable.HashMap()


private class _Prop (val name: String) :
  var propType: Any = uninitialized // TypeRepr[Any] or Type[] ???
  var getter: Option[Any] = None
  var setter: Option[Any] = None














