package org.mvv.scala.tools.props

import scala.language.implicitConversions
import org.mvv.scala.tools.{ equalImpl, isNull, isNotNull, isNullOrEmpty, ifNull, safe }

import scala.annotation.targetName


enum PropType derives CanEqual :
  case Regular, LateInit


implicit inline def fromPropertyValue[T](propValue: PropertyValue[T]): T = propValue.value

/** 1st param - new non-null value, 2nd param - previous/initial nullable value. */
// // T O D O: use these below with param names like (newValue: T, prevValue: T|Null)=>Unit
type ChangingValueFunc[T] = (T, T|Null) => Unit

private def noOpChangingF[T]: ChangingValueFunc[T] = {(_:T,_:T|Null)=>}


// This class is designed because kotlin does not support 'late init' props with custom getter/setter
class PropertyValue[T] (
  val propType: PropType,
  override val name: String,
  _value: T|Null = null,
  uninitializedValue: T|Null = null,

  val changeable: Boolean = true,

  /** !!! Message should have exactly ${prev} and ${new} (not short forms like $prev and $new) */
  // There Option (right design) is not used to make using class more comfortable.
  val changeErrorMessage: String|Null = null,

  val validate:   ChangingValueFunc[T] = noOpChangingF,
  val preUpdate:  ChangingValueFunc[T] = noOpChangingF,
  val postUpdate: ChangingValueFunc[T] = noOpChangingF,
) extends WritableProp[T] with Equals derives CanEqual :

  private var internalValue: T|Null = if _value.isNull then uninitializedValue else _value

  // T O D O: find better name (but seems 'uninitializable' is misprint)
  /** Returns unsafe/nullable/uninitialized value
   *  (nullable/unsafe in case if if 'uninitializedValue' is null). */
  //noinspection ScalaWeakerAccess
  def asNullableValue: T|Null = internalValue

  @throws(classOf[UninitializedPropertyAccessException])
  override def value: T =
    val finalValueRef = internalValue
    val isValidToReturn = propType match
      case PropType.Regular  => finalValueRef.isNotNull
      case PropType.LateInit => _isInitialized(finalValueRef)

    if isValidToReturn then finalValueRef.nn
    else throw UninitializedPropertyAccessException(s"Property [$name] is not initialized.")


  def isInitialized: Boolean =
    val finalValueRef = internalValue
    _isInitialized(finalValueRef)

  private def _isInitialized(valueRef: T|Null): Boolean =
    //noinspection ScalaUnusedSymbol
    given CanEqual[T|Null, T|Null] = CanEqual.derived
    valueRef.isNotNull && valueRef != uninitializedValue

  override def value_=(v: T): Unit =
    val prev = this.internalValue

    validateNonChangeable(v, prev)
    validate(v, prev)
    preUpdate(v, prev)
    internalValue = v
    postUpdate(v, prev)

  private def validateNonChangeable(newValue: T, prevValue: T|Null): Unit =
    //noinspection ScalaUnusedSymbol
    given CanEqual[T|Null, T|Null] = CanEqual.derived
    //noinspection ScalaUnusedSymbol
    given CanEqual[T, T|Null] = CanEqual.derived

    if newValue == prevValue then { return }

    def errMsg = changeErrorMessage.ifNull(defaultChangeErrorMessage)
      .replace("${prev}", String.valueOf(prevValue.safe))
      .nn.replace("${new}", String.valueOf(newValue.safe))

    // we do not allow to set value to null if it was already initialized/set to non-null value (for both Regular or LateInit)
    if newValue.isNull && prevValue.isNotNull
      then throw IllegalArgumentException(errMsg)

    //seems there is no difference between Regular and LateInit
    if !changeable && prevValue != uninitializedValue
      then throw IllegalArgumentException(errMsg)


  private def defaultChangeErrorMessage: String =
    if name.isNullOrEmpty
    then "Not allowed to change property (from [${prev}] to [${new}])."
    else s"Not allowed to change property [$name] (from [$${prev}] to [$${new}])."


  override def toString: String = s"$internalValue"
  override def canEqual(other: Any): Boolean = other.isInstanceOf[PropertyValue[?]]

  // in scala3 equals with 'match' causes warning "pattern selector should be an instance of Matchable"
  override def equals(other: Any): Boolean =
    import org.mvv.scala.tools.AnyCanEqualGivens.given
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl[PropertyValue[?]](this, other) { _.asNullableValue == _.asNullableValue }

  /** !!! It is not designed to be map key !!! */
  //noinspection HashCodeUsesVar
  override def hashCode: Int =
    val finalSafeRef = this.internalValue
    if finalSafeRef.isNull then 42.hashCode else finalSafeRef.hashCode

  def apply(value: T): PropertyValue[T] = { value_=(value); this }
  //@targetName("assignOp")
  //infix def `=` (value: T): Unit = value_=(value)

end PropertyValue


class UninitializedPropertyAccessException (msg: String) extends RuntimeException(msg)
