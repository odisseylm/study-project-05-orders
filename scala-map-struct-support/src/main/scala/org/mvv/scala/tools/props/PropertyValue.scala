package org.mvv.scala.tools.props

import org.mvv.scala.tools.{ equalImpl, isNullOrEmpty, ifNull, safe }



/** 1st param - new non-null value, 2nd param - previous/initial nullable value. */
// // T O D O: use these below with param names like (newValue: T, prevValue: T|Null)=>Unit
type ChangingValueFunc[T] = (T, T|Null) => Unit

private def noOpChangingF[T]: ChangingValueFunc[T] = {(_:T,_:T|Null)=>}


// This class is designed because kotlin does not support 'late init' props with custom getter/setter
class PropertyValue[T] (
  override val name: String,
  _value: T|Null = null,

  val changeable: Boolean = true,

  /** !!! Message should have exactly ${prev} and ${new} (not short forms like $prev and $new) */
  val changeErrorMessage: String|Null = null,

  val validate:   ChangingValueFunc[T] = noOpChangingF,
  val preUpdate:  ChangingValueFunc[T] = noOpChangingF,
  val postUpdate: ChangingValueFunc[T] = noOpChangingF,
) extends WritableProp[T] with Equals derives CanEqual :

  private var internalValue: T|Null = _value

  //noinspection ScalaWeakerAccess
  def asNullableValue: T|Null = internalValue

  @throws(classOf[UninitializedPropertyAccessException])
  override def value: T =
    val finalValueRef = internalValue
    if finalValueRef != null
    then finalValueRef.nn
    else throw UninitializedPropertyAccessException(s"Property [$name] is not initialized yet.")

  def isInitialized: Boolean = internalValue != null

  override def value_=(v: T): Unit =
    val prev = this.internalValue

    validateNonChangeable(v, prev)
    validate(v, prev)
    preUpdate(v, prev)
    internalValue = v
    postUpdate(v, prev)

  private def validateNonChangeable(newValue: T, prevValue: T|Null): Unit =
    if !changeable && prevValue != null && newValue != prevValue then
      val msg = changeErrorMessage.ifNull(defaultChangeErrorMessage)
          .replace("${prev}", prevValue.safe.toString())
          .nn.replace("${new}", newValue.safe.toString())
      throw IllegalStateException(msg)


  private def defaultChangeErrorMessage: String =
    if name.isNullOrEmpty
    then "Not allowed to change property (from [${prev}] to [${new}])."
    else "Not allowed to change property '$propName' (from [${prev}] to [${new}])."


  override def toString: String = s"$internalValue"
  override def canEqual(other: Any): Boolean = other.isInstanceOf[PropertyValue[?]]

  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = (other : @unchecked) match
  //    case that: LateInitProperty[T,Owner] => that.canEqual(this) && that.asNullableValue == this.asNullableValue
  //    case _ => false

  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl[PropertyValue[?]](this, other) { _.asNullableValue == _.asNullableValue }

  /** !!! It is not designed to be map key !!! */
  //noinspection HashCodeUsesVar
  override def hashCode: Int =
    val finalSafeRef = this.internalValue
    if finalSafeRef == null then 42.hashCode else finalSafeRef.hashCode

  //def apply(value: T): Unit = set(value)
  //def `=` (value: T): Unit = set(value)

end PropertyValue


class UninitializedPropertyAccessException (msg: String) extends RuntimeException(msg)
