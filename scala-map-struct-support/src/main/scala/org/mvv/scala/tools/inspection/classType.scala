package org.mvv.scala.tools.inspection




//noinspection ScalaFileName
trait _Class :
  def fullName: String
  def _package: String
  def simpleName: String

  def classKind: ClassKind
  // with current impl it possibly can have duplicates
  def parentTypes: List[_Type]

  def fields: Map[_FieldKey, _Field]
  def methods: Map[_MethodKey, _Method]
  def declaredFields: Map[_FieldKey, _Field]
  def declaredMethods: Map[_MethodKey, _Method]

  def runtimeClass: Option[Class[?]]


//noinspection ScalaFileName
object _Class :
  def apply(
    fullName: String,
    _package: String,
    simpleName: String,
    classKind: ClassKind,
    // with current impl it possibly can have duplicates
    parentTypes: List[_Type],

    fields: Map[_FieldKey, _Field],
    methods: Map[_MethodKey, _Method],

    declaredFields: Map[_FieldKey, _Field],
    declaredMethods: Map[_MethodKey, _Method],

    runtimeClass: Option[Class[?]],
  ): _Class =
    _ClassImpl(
      fullName,
      _package,
      simpleName,

      classKind,
      // with current impl it possibly can have duplicates
      parentTypes,

      fields,
      methods,
      declaredFields,
      declaredMethods,

      runtimeClass,
    )

  private class _ClassImpl (
    val fullName: String,
    val _package: String,
    val simpleName: String,
    val classKind: ClassKind,

    // with current impl it possibly can have duplicates
    val parentTypes: List[_Type],

    val fields: Map[_FieldKey, _Field],
    val methods: Map[_MethodKey, _Method],
    val declaredFields: Map[_FieldKey, _Field],
    val declaredMethods: Map[_MethodKey, _Method],

    val runtimeClass: Option[Class[?]],
  ) extends _Class
