package com.mvv.scala.props

//noinspection ScalaUnusedSymbol
def temp894758454795748(): Unit = {}
//noinspection ScalaUnusedSymbol
def temp894758454795749(): Unit = {}

/*
import scala.annotation.nowarn
//
import com.mvv.log.safe
import com.mvv.nullables.ifNull
import com.mvv.utils.isNullOrEmpty
import com.mvv.utils.equalImpl


class UninitializedPropertyAccessException (msg: String) extends RuntimeException(msg)

trait KProperty[T] :
  def name: String
  def value: T

object KProperty :
  def simpleProperty[T](propertyName: String): KProperty[T] = SimplePropertyImpl(propertyName, valueNotSupported)
  def simpleProperty[T](propertyName: String, value: ()=>T): KProperty[T] =
    SimplePropertyImpl(propertyName, value)

private def valueNotSupported[T]: ()=>T = () => throw IllegalStateException("Getting value is not implemented/supported.")

private case class SimplePropertyImpl[T] (
  name: String,
  valueF: ()=>T,
  ) extends KProperty[T] :
  override def value: T = valueF()


// from Kotlin
trait ReadOnlyProperty[/*in*/ T, /*out*/ V] :
  def getValue(thisRef: T, property: KProperty[?]): V

trait ReadWriteProperty[/*in*/ T, V] extends ReadOnlyProperty[T, V] :
  def setValue(thisRef: T, property: KProperty[?], value: V): Unit



// This class is designed because kotlin does not support 'late init' props with custom getter/setter
class LateInitProperty[T, Owner] (
    value: T|Null = null,
    val propName: String|Null = null, // T O D O: maybe Option ???

    val changeable: Boolean = true,
    // !!! Message should have exactly ${prev} and ${new} (not short forms like $prev and $new)
    val changeErrorMessage: String|Null = null,

    val validate:   (T, T|Null)=>Unit = {(n:T,p:T|Null)=>},
    val preUpdate:  (T, T|Null)=>Unit = {(n:T,p:T|Null)=>},
    val postUpdate: (T, T|Null)=>Unit = {(n:T,p:T|Null)=>},
    // T O D O: use these below with param names
    //val validate:   (newValue: T, prevValue: T|Null)=>Unit = {(n,p)=>},
    //val preUpdate:  (newValue: T, prevValue: T|Null)=>Unit = {(n,p)=>},
    //val postUpdate: (newValue: T, prevValue: T|Null)=>Unit = {(n,p)=>},
) extends ReadWriteProperty[Owner, T] with Equals derives CanEqual {
    import com.mvv.nullables.AnyCanEqualGivens.given
    private var internalValue: T|Null = value
    def asNullableValue: T|Null = internalValue
    def asNonNullableValue: T = internalValue.nn
    def set(v: T): Unit = {
        val prev = this.internalValue
        validateNonChangeable(v, prev)
        validate(v, prev)
        preUpdate(v, prev)
        internalValue = v
        postUpdate(v, prev)
    }

    private def validateNonChangeable(newValue: T, prevValue: T|Null): Unit =
        if (!changeable && prevValue != null && newValue != prevValue)
            val msg = changeErrorMessage.ifNull(defaultChangeErrorMessage)
                .replace("${prev}", prevValue.safe.toString())
                .nn.replace("${new}", newValue.safe.toString())
            throw IllegalStateException(msg)

    private def defaultChangeErrorMessage: String =
        if propName.isNullOrEmpty then "Not allowed to change property (from [${prev}] to [${new}])."
        else "Not allowed to change property '$propName' (from [${prev}] to [${new}])."


    // T O D O: add logic to verify value on null only if T is nullable. Is it needed???
    override /*operator*/ def getValue(thisRef: Owner, property: KProperty[?]): T =
        val finalValueRef = asNullableValue
        if finalValueRef != null then finalValueRef.nn
        else throw UninitializedPropertyAccessException(s"Property [$propName] is not initialized yet.")

    override /*operator*/ def setValue(thisRef: Owner, property: KProperty[?], value: T): Unit = set(value)

    //???

    override def toString: String = "$internalValue"
    override def canEqual(other: Any): Boolean = (other : @unchecked).isInstanceOf[LateInitProperty[T, Owner]]
    // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
    //override def equals(other: Any): Boolean = (other : @unchecked) match
    //    case that: LateInitProperty[T,Owner] => that.canEqual(this) && that.asNullableValue == this.asNullableValue
    //    case _ => false
    override def equals(other: Any): Boolean =
      // it is inlined and have resulting byte code similar to code with 'match'
      equalImpl(this, other) { _.asNullableValue == _.asNullableValue }

    //noinspection HashCodeUsesVar // It is not designed to be map key
    override def hashCode: Int =
        val finalSafeRef = this.internalValue
        if finalSafeRef == null then 42.hashCode else finalSafeRef.hashCode

    def apply(value: T): Unit = set(value)
    def `=` (value: T): Unit = set(value)
}

//object LateInitProperty :
//  given aaa: scala.Conversion[T, LateInitProperty[T, ?]] = {
//    def apply(x: T): U
//  }
*/