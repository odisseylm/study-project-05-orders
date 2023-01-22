//noinspection ScalaUnusedSymbol
package com.mvv.nullables


//given givenCanEqual_CharSequence_Null: CanEqual[CharSequence, Null] = CanEqual.derived
//given givenCanEqual_CharSequenceNull_Null: CanEqual[CharSequence|Null, Null] = CanEqual.derived
//given givenCanEqual_CharSequenceNull_CharSequence: CanEqual[CharSequence|Null, CharSequence] = CanEqual.derived
//given givenCanEqual_Null_CharSequence: CanEqual[Null, CharSequence] = CanEqual.derived
//given givenCanEqual_Null_CharSequenceNull: CanEqual[Null, CharSequence|Null] = CanEqual.derived
//given givenCanEqual_CharSequence_CharSequenceNull: CanEqual[CharSequence, CharSequence|Null] = CanEqual.derived
//given givenCanEqual_CharSequenceNull_CharSequenceNull: CanEqual[CharSequence|Null, CharSequence|Null] = CanEqual.derived



// Do NOT import them to top level, only inside class/method
object AnyCanEqualGivens extends NullableCanEqualGivens[Any]
object AnyRefCanEqualGivens extends NullableCanEqualGivens[AnyRef]


object CharSequenceCanEqualGivens extends NullableCanEqualGivens[java.lang.CharSequence]
object StringCanEqualGivens extends NullableCanEqualGivens[java.lang.String]


object DayOfWeekCanEqualGivens extends NullableCanEqualGivens[java.time.DayOfWeek]
object DurationCanEqualGivens extends NullableCanEqualGivens[java.time.Duration]
object InstantCanEqualGivens extends NullableCanEqualGivens[java.time.Instant]
object LocalDateCanEqualGivens extends NullableCanEqualGivens[java.time.LocalDate]
object LocalDateTimeCanEqualGivens extends NullableCanEqualGivens[java.time.LocalDateTime]
object LocalTimeCanEqualGivens extends NullableCanEqualGivens[java.time.LocalTime]
object MonthCanEqualGivens extends NullableCanEqualGivens[java.time.Month]
object MonthDayCanEqualGivens extends NullableCanEqualGivens[java.time.MonthDay]
object OffsetDateTimeCanEqualGivens extends NullableCanEqualGivens[java.time.OffsetDateTime]
object OffsetTimeCanEqualGivens extends NullableCanEqualGivens[java.time.OffsetTime]
object PeriodCanEqualGivens extends NullableCanEqualGivens[java.time.Period]
//object SerCanEqualGivens extends NullableCanEqualGivens[java.time.Ser]
object YearCanEqualGivens extends NullableCanEqualGivens[java.time.Year]
object YearMonthCanEqualGivens extends NullableCanEqualGivens[java.time.YearMonth]
object ZonedDateTimeCanEqualGivens extends NullableCanEqualGivens[java.time.ZonedDateTime]
object ZoneIdCanEqualGivens extends NullableCanEqualGivens[java.time.ZoneId]
object ZoneOffsetCanEqualGivens extends NullableCanEqualGivens[java.time.ZoneOffset]
//object ZoneRegionCanEqualGivens extends NullableCanEqualGivens[java.time.ZoneRegion] ??? TODO: ???


// T O D O: how to reexport if it is possible
// T O D O: put all CanEqual givens there (except general Any/AnyRef)
//object AllCanEqualGivens :
//  import CharSequenceCanEqualGivens.given
//  import StringCanEqualGivens.given

//object AllCanEqualGivens22
//  extends CharSequenceCanEqualGivens
//  //with    StringCanEqualGivens

