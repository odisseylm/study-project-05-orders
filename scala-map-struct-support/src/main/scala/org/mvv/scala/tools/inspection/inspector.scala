package org.mvv.scala.tools.inspection


//noinspection ScalaFileName
trait ScalaBeanInspector :
  def classesDescr: Map[String, _Class]
  def classDescr(classFullName: String): Option[_Class]

  def inspectClass(cls: Class[?]): _Class
  def inspectClass(fullClassName: String): _Class



//noinspection ScalaFileName
object ScalaBeanInspector :
  def createLight(): ScalaBeanInspector = org.mvv.scala.tools.inspection.light.LightScalaBeanInspector()
