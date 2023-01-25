package com.mvv.scala.macros

import scala.annotation.{nowarn, targetName}
import scala.reflect.ClassTag


trait PropValue[PropertyType, OwnerType] :
  def propName: String
  def value: Option[PropertyType]
  // currently owner will be Any (probably later I'll find how to fill it properly)
  def propOwner: Option[Class[OwnerType]] // TODO: rename to propOwnerType


private class PropValueImpl[PropertyType, OwnerType] (
  val propName: String,
  val value: Option[PropertyType],
  val propOwner: Option[Class[OwnerType]],
) extends PropValue[PropertyType, OwnerType]


object PropValue :
  def apply[T, O](propName: String, propValue: T|Null, propOwner: Class[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), toOption(propOwner))
  def apply[T, O](propName: String, propValue: Option[T], propOwner: Class[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), toOption(propOwner))

  // Used in macros. Macros cannot return current compiling class as Class, only as ClassTag
  def apply[T, O](propName: String, propValue: T|Null, propOwner: ClassTag[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), classTagToClass(propOwner))
  def apply[T, O](propName: String, propValue: Option[T], propOwner: ClassTag[O]|Null): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), classTagToClass(propOwner))

  def apply[P, O](propName: String, propValue: P|Null): PropValue[P, O] =
    new PropValueImpl(propName, toOption(propValue), None)
  def apply[T, O](propName: String, propValue: Option[T]): PropValue[T, O] =
    new PropValueImpl(propName, toOption(propValue), None)


private def classTagToClass[T](classTag: ClassTag[T]|Null): Option[Class[T]] =
  toOption[ClassTag[T]](classTag).map(_.runtimeClass.asInstanceOf[Class[T]])

@nowarn("msg=cannot be checked at runtime")
//noinspection TypeCheckCanBeMatch,DuplicatedCode,IsInstanceOf
private def toOption[T](@nowarn v: T|Null|Option[T]): Option[T] =
  if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]] else Option[T](v.asInstanceOf[T])



trait ReadonlyProp[PropertyType, OwnerType] :
  def isNullable: Boolean
  /** optional */
  def propOwner: Option[OwnerType]
  /** optional */
  def propOwnerType: Option[Class[OwnerType]]
  def propName: String
  /** Use it for non-nullable values.
   *  Use asOption method for nullable type.
   *  If property is non-nullable it will throw NPE in case of null value. */
  @throws[NullPointerException]
  def value: PropertyType
  def asOption: Option[PropertyType]
  /** Can be useful only if you use explicit-null feature.
   *  It would be useful due to its explicit-null nullable return type.
   */
  def asNullable: PropertyType|Null


trait WritableProp[PropertyType, OwnerType] extends ReadonlyProp[PropertyType, OwnerType]:
  // if property is non-nullable it will throw NPE in case of passing null value
  @throws[NullPointerException]
  def set(v: PropertyType): Unit
  /** Can be useful only if you use explicit-null feature.
   *  It would be useful due to its explicit-null nullable method signature (you will not need to do type casting).
   */
  def setNullable(v: PropertyType|Null): Unit


private class ReadonlyPropImpl[PropertyType, OwnerType] (
    override val isNullable: Boolean = true,
    override val propName: String,
    val getter: ()=>PropertyType|Null,
    override val propOwner: Option[OwnerType],
    override val propOwnerType: Option[Class[OwnerType]],
  ) extends ReadonlyProp[PropertyType, OwnerType] :

  private def getValueOrNull: PropertyType|Null = getter()

  override def value: PropertyType =
    val v = getValueOrNull
    if isNullable then v.castToNonNullable
    else checkNotNull[PropertyType](v, s"Property $propName is null.")

  override def asOption: Option[PropertyType] = Option(getValueOrNull.castToNonNullable)
  override def asNullable: PropertyType|Null = this.value


private class WritablePropImpl[PropertyType, OwnerType] (
    isNullable: Boolean = true,
    propName: String,
    getter: () => PropertyType|Null,
    val setter: (PropertyType|Null) => Unit,
    propOwner: Option[OwnerType],
    propOwnerType: Option[Class[OwnerType]],
  )
  extends ReadonlyPropImpl[PropertyType, OwnerType](isNullable, propName, getter, propOwner, propOwnerType)
     with WritableProp[PropertyType, OwnerType] :
  @throws[NullPointerException]
  override def set(v: PropertyType): Unit =
    if (!isNullable) requireNotNull(v, s"Value of $propName is null.")
    setter(v)
  override def setNullable(v: PropertyType|Null): Unit = set(v.castToNonNullable)


object Property :
  // Readonly property
  def property[T, O](isNullable: Boolean, propName: String, getter: () => T|Null,
    propOwner: O|Null, propOwnerType: Class[O]|Null): ReadonlyProp[T,O] =
    new ReadonlyPropImpl(isNullable, propName, getter, toOption(propOwner), toOption(propOwnerType))
  @targetName("propertyForOption")
  def property[T, O](isNullable: Boolean, propName: String, getter: () => Option[T],
    propOwner: O|Null, propOwnerType: Class[O]|Null): ReadonlyProp[T,O] =
    new ReadonlyPropImpl(isNullable, propName, () => getter().orNull, toOption(propOwner), toOption(propOwnerType))

  // Used in macros. Macros cannot return current compiling class as Class, only as ClassTag
  def property[T, O](isNullable: Boolean, propName: String, getter: () => T|Null,
    propOwner: O|Null, propOwnerType: ClassTag[O]|Null): ReadonlyProp[T,O] =
    new ReadonlyPropImpl(isNullable, propName, getter, toOption(propOwner), classTagToClass(propOwnerType))
  @targetName("propertyForOption2")
  def property[T, O](isNullable: Boolean, propName: String, getter: () => Option[T],
    propOwner: O|Null, propOwnerType: ClassTag[O]|Null): ReadonlyProp[T,O] =
    new ReadonlyPropImpl(isNullable, propName, () => getter().orNull, toOption(propOwner), classTagToClass(propOwnerType))

  // Writable property
  def property[T, O](isNullable: Boolean, propName: String, getter: () => T|Null, setter: (T|Null)=>Unit,
    propOwner: O|Null, propOwnerType: Class[O]|Null): WritableProp[T,O] =
    new WritablePropImpl(isNullable, propName, getter, setter, toOption(propOwner), toOption(propOwnerType))
  @targetName("propertyForOption")
  def property[T, O](isNullable: Boolean, propName: String, getter: () => Option[T], setter: Option[T]=>Unit,
    propOwner: O|Null, propOwnerType: Class[O]|Null): WritableProp[T,O] =
    new WritablePropImpl(isNullable, propName, () => getter().orNull, (v: T|Null) => setter(Option(v.castToNonNullable)),
      toOption(propOwner), toOption(propOwnerType))

  // Used in macros. Macros cannot return current compiling class as Class, only as ClassTag
  def property[T, O](isNullable: Boolean, propName: String, getter: () => T|Null, setter: (T|Null)=>Unit,
    propOwner: O|Null, propOwnerType: ClassTag[O]|Null): WritableProp[T,O] =
    new WritablePropImpl(isNullable, propName, getter, setter, toOption(propOwner), classTagToClass(propOwnerType))
  @targetName("propertyForOption")
  def property[T, O](isNullable: Boolean, propName: String, getter: () => Option[T], setter: Option[T]=>Unit,
    propOwner: O|Null, propOwnerType: ClassTag[O]|Null): WritableProp[T,O] =
    new WritablePropImpl(isNullable, propName, () => getter().orNull, (v: T|Null) => setter(Option(v.castToNonNullable)),
      toOption(propOwner), classTagToClass(propOwnerType))


// utils, move to somewhere
extension [T](v: T|Null)
  //noinspection ScalaUnusedSymbol
  private def castToNonNullable: T = v.asInstanceOf[T]

private def checkNotNull[T](v: T|Null, msg: =>String): T =
  if (v == null) throw IllegalStateException(msg)
  v.asInstanceOf[T]

private def requireNotNull[T](v: T|Null, msg: =>String): T =
  require(v != null, msg)
  v.asInstanceOf[T]
