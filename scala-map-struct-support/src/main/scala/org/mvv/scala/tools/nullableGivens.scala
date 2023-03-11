package org.mvv.scala.tools



//noinspection ScalaFileName
trait NullableCanEqualGivens[T] :
  given givenCanEqual_Type_Type: CanEqual[T, T] = CanEqual.derived
  given givenCanEqual_Type_Null: CanEqual[T, Null] = CanEqual.derived
  given givenCanEqual_TypeNull_Null: CanEqual[T|Null, Null] = CanEqual.derived
  given givenCanEqual_TypeNull_Type: CanEqual[T|Null, T] = CanEqual.derived
  given givenCanEqual_Null_Type: CanEqual[Null, T] = CanEqual.derived
  given givenCanEqual_Null_TypeNull: CanEqual[Null, T|Null] = CanEqual.derived
  given givenCanEqual_Type_TypeNull: CanEqual[T, T|Null] = CanEqual.derived
  given givenCanEqual_TypeNull_TypeNull: CanEqual[T|Null, T|Null] = CanEqual.derived
