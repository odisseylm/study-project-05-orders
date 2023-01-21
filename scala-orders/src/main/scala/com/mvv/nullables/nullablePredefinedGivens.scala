package com.mvv.nullables


//given givenCanEqual_CharSequence_Null: CanEqual[CharSequence, Null] = CanEqual.derived
//given givenCanEqual_CharSequenceNull_Null: CanEqual[CharSequence|Null, Null] = CanEqual.derived
//given givenCanEqual_CharSequenceNull_CharSequence: CanEqual[CharSequence|Null, CharSequence] = CanEqual.derived
//given givenCanEqual_Null_CharSequence: CanEqual[Null, CharSequence] = CanEqual.derived
//given givenCanEqual_Null_CharSequenceNull: CanEqual[Null, CharSequence|Null] = CanEqual.derived
//given givenCanEqual_CharSequence_CharSequenceNull: CanEqual[CharSequence, CharSequence|Null] = CanEqual.derived
//given givenCanEqual_CharSequenceNull_CharSequenceNull: CanEqual[CharSequence|Null, CharSequence|Null] = CanEqual.derived


trait AnyCanEqualGivens extends NullableCanEqualGivens[Any]
object AnyCanEqualGivens extends AnyCanEqualGivens

trait AnyRefCanEqualGivens extends NullableCanEqualGivens[AnyRef]
object AnyRefCanEqualGivens extends AnyRefCanEqualGivens

trait CharSequenceCanEqualGivens extends NullableCanEqualGivens[java.lang.CharSequence]
object CharSequenceCanEqualGivens extends CharSequenceCanEqualGivens

trait StringCanEqualGivens extends NullableCanEqualGivens[java.lang.String]
object StringCanEqualGivens extends StringCanEqualGivens


// TODO: how to reexport if it is possible
// TODO: put all CanEqual givens there (except general Any/AnyRef)
//object AllCanEqualGivens :
//  import CharSequenceCanEqualGivens.given
//  import StringCanEqualGivens.given

//object AllCanEqualGivens22
//  extends CharSequenceCanEqualGivens
//  //with    StringCanEqualGivens

