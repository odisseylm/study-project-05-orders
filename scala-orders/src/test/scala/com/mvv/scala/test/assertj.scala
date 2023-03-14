package com.mvv.scala.test

import org.assertj.core.api.*
import org.assertj.core.api.SoftAssertions as AssertJSoftAssertions
import org.assertj.core.api.Java6StandardSoftAssertionsProvider as BaseSA

import java.io.IOException
import java.net.{ URI, URL }
import java.util
import scala.reflect.ClassTag
import scala.collection.mutable
import scala.collection.immutable



object assertj :

  extension (sa: AssertJSoftAssertions)
    def runTests(init: AssertJSoftAssertions ?=> Unit): AssertJSoftAssertions =
      given _sa: org.assertj.core.api.SoftAssertions = sa
      //noinspection ScalaUnusedExpression, it is really called 'by-name'
      init
      sa


  object kotlinStyle:
    /**
     *  Designed for easy migration of kotlin tests with {{{ SoftAssertions().apply { ... }.assertAll() }}}.
     *  For your new tests is better to use straightforward approach
     *  {{{ org.assertj.core.api.SoftAssertions().runTests { ... }.assertAll() }}}
     */
    def SoftAssertions(): AssertJSoftAssertions = new AssertJSoftAssertions()

    extension (sa: AssertJSoftAssertions)
      // it may not be picked up from extension :-(
      def apply(init: AssertJSoftAssertions ?=> Unit): AssertJSoftAssertions =
        given _sa: AssertJSoftAssertions = sa
        //noinspection ScalaUnusedExpression, it is really called 'by-name'
        init
        sa


  object JScalaAssertions:
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    inline def assertThatJavaCode(shouldRaiseOrNotThrowable: ThrowableAssert.ThrowingCallable): AbstractThrowableAssert[?, ? <: Throwable] =
      org.assertj.core.api.Assertions.assertThatCode(shouldRaiseOrNotThrowable)
    inline def assertThatCode(shouldRaiseOrNotThrowable: => Any): AbstractThrowableAssert[?, ? <: Throwable] =
      org.assertj.core.api.Assertions.assertThatCode(() => shouldRaiseOrNotThrowable)

    // scala collections
    inline def assertThat[T](actual: List[? <: T]): ListAssert[T] =
      org.assertj.core.api.Assertions.assertThat[T](actual.asJava)
    inline def assertThat[T](actual: mutable.ArrayBuffer[? <: T]): ListAssert[T] =
      org.assertj.core.api.Assertions.assertThat[T](actual.asJava)
    inline def assertThat[K, V](actual: Map[K, V]): MapAssert[K, V] =
      org.assertj.core.api.Assertions.assertThat[K, V](actual.asJava)
    inline def assertThat[K, V](actual: mutable.Map[K, V]): MapAssert[K, V] =
      org.assertj.core.api.Assertions.assertThat[K, V](actual.asJava)


  object ImplicitSoftAssertions :
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    // It does not work at all :-( Scala does not use/inline generic type (as in C++)
    //transparent inline def assertThat[T](inline v: T)(using sa: org.assertj.core.api.SoftAssertions): Any = sa.assertThat(v)

    // These approaches works perfectly with scala compiler... But IDE does not show you any tips :-(
    //transparent inline def assertThat(v: Int)(using sa: org.assertj.core.api.SoftAssertions): Any = sa.assertThat(v)
    //transparent inline def assertThat(v: String)(using sa: org.assertj.core.api.SoftAssertions): Any = sa.assertThat(v)


    // base types
    inline def assertThat[T](actual: T)(using sa: BaseSA): ObjectAssert[T] = sa.assertThat[T](actual)

    inline def assertThatObject[T](actual: T)(using sa: BaseSA): ObjectAssert[T] =
      sa.assertThatObject(actual)

    // comparable
    inline def assertThat[T <: Comparable[T]](actual: T)(using sa: BaseSA): AbstractComparableAssert[?, T] =
      sa.assertThat[T](actual)
    inline def assertThatComparable[T](actual: Comparable[T])(using sa: BaseSA): AbstractUniversalComparableAssert[?, T] =
      sa.assertThatComparable[T](actual)

    // other
    inline def assertThat(actual: URI)(using sa: BaseSA): UriAssert = sa.assertThat(actual)
    inline def assertThat(actual: URL)(using sa: BaseSA): AbstractUrlAssert[?] = sa.assertThat(actual)

    // java collections
    inline def assertThat[T](actual: util.Collection[? <: T])(using sa: BaseSA): CollectionAssert[T] =
      sa.assertThat[T](actual)
    inline def assertThatCollection[T](actual: util.Collection[? <: T])(using sa: BaseSA): CollectionAssert[T] =
      sa.assertThatCollection[T](actual)

    inline def assertThat[T](actual: Array[T])(using sa: BaseSA): ObjectArrayAssert[T] =
      sa.assertThat(actual.asInstanceOf[Array[Object & T | Null]]) // hm... strange required casting

    inline def assertThat[T](actual: java.lang.Iterable[? <: T])(using sa: BaseSA): IterableAssert[T] =
      sa.assertThat[T](actual)
    inline def assertThatIterable[ELEMENT](actual: java.lang.Iterable[? <: ELEMENT])(using sa: BaseSA): IterableAssert[ELEMENT] =
      sa.assertThatIterable[ELEMENT](actual)
    inline def assertThat[T](actual: util.Iterator[? <: T])(using sa: BaseSA): IteratorAssert[T] =
      sa.assertThat[T](actual)
    inline def assertThatIterator[ELEMENT](actual: util.Iterator[? <: ELEMENT])(using sa: BaseSA): IteratorAssert[ELEMENT] =
      sa.assertThatIterator[ELEMENT](actual)

    inline def assertThat[T](actual: util.List[? <: T])(using sa: BaseSA): ListAssert[T] = sa.assertThat[T](actual)
    inline def assertThatList[ELEMENT](actual: util.List[? <: ELEMENT])(using sa: BaseSA): ListAssert[ELEMENT] = sa.assertThatList[ELEMENT](actual)
    inline def assertThat[K, V](actual: util.Map[K, V])(using sa: BaseSA): MapAssert[K, V] = sa.assertThat(actual)


    // scala collections
    inline def assertThat[T](actual: List[? <: T])(using sa: BaseSA): ListAssert[T] =
      sa.assertThat[T](actual.asJava)
    inline def assertThat[T](actual: mutable.ArrayBuffer[? <: T])(using sa: BaseSA): ListAssert[T] =
      sa.assertThat[T](actual.asJava)
    inline def assertThat[K,V](actual: Map[K,V])(using sa: BaseSA): MapAssert[K,V] =
      sa.assertThat[K,V](actual.asJava)
    inline def assertThat[K,V](actual: mutable.Map[K,V])(using sa: BaseSA): MapAssert[K,V] =
      sa.assertThat[K,V](actual.asJava)


    // primitives
    inline def assertThat(actual: Byte)(using sa: BaseSA): ByteAssert = sa.assertThat(actual)
    inline def assertThat(actual: Short)(using sa: BaseSA): ShortAssert = sa.assertThat(actual)
    inline def assertThat(actual: Int)(using sa: BaseSA): IntegerAssert = sa.assertThat(actual)
    inline def assertThat(actual: Long)(using sa: BaseSA): LongAssert = sa.assertThat(actual)
    inline def assertThat(actual: Float)(using sa: BaseSA): FloatAssert = sa.assertThat(actual)
    inline def assertThat(actual: Double)(using sa: BaseSA): DoubleAssert = sa.assertThat(actual)
    inline def assertThat(actual: Char)(using sa: BaseSA): CharacterAssert = sa.assertThat(actual)
    inline def assertThat(actual: String)(using sa: BaseSA): StringAssert = sa.assertThat(actual)
    inline def assertThat(actual: CharSequence)(using sa: BaseSA): CharSequenceAssert = sa.assertThat(actual)
    inline def assertThat(actual: StringBuilder)(using sa: BaseSA): CharSequenceAssert = sa.assertThat(actual)


    // exceptions
    inline def assertThatJavaCode(shouldRaiseOrNotThrowable: ThrowableAssert.ThrowingCallable)(using sa: BaseSA): AbstractThrowableAssert[?, ? <: Throwable] =
      sa.assertThatCode(shouldRaiseOrNotThrowable)
    inline def assertThatCode(shouldRaiseOrNotThrowable: => Any)(using sa: BaseSA): AbstractThrowableAssert[?, ? <: Throwable] =
      //sa.assertThatCode(new ThrowableAssert.ThrowingCallable { def call(): Unit = shouldRaiseOrNotThrowable } )
      sa.assertThatCode(() => shouldRaiseOrNotThrowable )

    inline def assertThat[T <: Throwable](actual: T)(using sa: BaseSA): ThrowableAssert[T] = sa.assertThat(actual)
    inline def assertThatThrownBy(shouldRaiseThrowable: ThrowableAssert.ThrowingCallable)(using sa: BaseSA): AbstractThrowableAssert[?, ? <: Throwable] =
      sa.assertThatThrownBy(shouldRaiseThrowable)
    inline def assertThatThrownBy(shouldRaiseThrowable: ThrowableAssert.ThrowingCallable, description: String, args: Any*)(using sa: BaseSA): AbstractThrowableAssert[?, ? <: Throwable]
      = sa.assertThatThrownBy(shouldRaiseThrowable, description, args*)

    inline def assertThatExceptionOfType_[T <: Throwable](throwableType: Class[T])(using sa: BaseSA): ThrowableTypeAssert[T] =
      sa.assertThatExceptionOfType(throwableType)
    inline def assertThatExceptionOfType[T <: Throwable](using sa: BaseSA)(implicit classTag: ClassTag[T]): ThrowableTypeAssert[T] =
      sa.assertThatExceptionOfType(classTag.runtimeClass.asInstanceOf[Class[T]])

    inline def assertThatRuntimeException()(using sa: BaseSA): ThrowableTypeAssert[RuntimeException] =
      sa.assertThatRuntimeException()
    inline def assertThatNullPointerException()(using sa: BaseSA): ThrowableTypeAssert[NullPointerException] =
      sa.assertThatNullPointerException()
    inline def assertThatIllegalArgumentException()(using sa: BaseSA): ThrowableTypeAssert[IllegalArgumentException] =
      sa.assertThatIllegalArgumentException()
    inline def assertThatIllegalStateException()(using sa: BaseSA): ThrowableTypeAssert[IllegalStateException] =
      sa.assertThatIllegalStateException()
    inline def assertThatIOException()(using sa: BaseSA): ThrowableTypeAssert[IOException] =
      sa.assertThatIOException()


    // others which can be delegated too
    //
    // DateAssert, AtomicBooleanAssert, AtomicIntegerAssert, AtomicIntegerArrayAssert,
    // AtomicIntegerFieldUpdaterAssert, AtomicLongAssert, AtomicLongArrayAssert, AtomicLongFieldUpdaterAssert,
    // AtomicReferenceAssert, AtomicReferenceArrayAssert, AtomicReferenceFieldUpdaterAssert,
    // AtomicMarkableReferenceAssert, AtomicStampedReferenceAssert
    //
    // arrays and 2D arrays
    //
    // assertThatReflectiveOperationException, assertThatIndexOutOfBoundsException,



// compilation test
@main def runJAssertSoftAssertionInKotlinStyle(): Unit = {
  import scala.language.unsafeNulls
  import assertj.kotlinStyle.{ SoftAssertions, apply }
  import assertj.ImplicitSoftAssertions.*

  def error1() = throw IllegalArgumentException("1234")

  SoftAssertions().apply {
    println("softAssertions58")
    assertThat(58).isEqualTo(58)
    assertThat("58").contains("8")
  }.assertAll()

  SoftAssertions().apply {
    assertThatJavaCode { () => error1() }
      .hasMessage("1234")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
  }.assertAll()

  SoftAssertions().apply {
    assertThatCode { val v = 789; print(v); error1() }
      .hasMessage("1234")
      .isExactlyInstanceOf(classOf[IllegalArgumentException])
  }.assertAll()
}
