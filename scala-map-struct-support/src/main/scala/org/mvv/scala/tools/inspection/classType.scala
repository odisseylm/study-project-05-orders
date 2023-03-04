package org.mvv.scala.tools.inspection




//noinspection ScalaFileName
trait _Class :
  def fullName: String
  def _package: String
  def simpleName: String
  def classKind: ClassKind
  // with current impl it possibly can have duplicates
  def parentTypes: List[_Type]

  def methods: Map[_MethodKey, _Method]
  def fields: Map[_FieldKey, _Field]

  def declaredFields: Map[_FieldKey, _Field]
  def declaredMethods: Map[_MethodKey, _Method]

  def runtimeClass: Option[Class[?]]
