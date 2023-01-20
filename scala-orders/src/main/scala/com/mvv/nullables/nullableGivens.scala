package com.mvv.nullables


given givenCanEqual_CharSequence_Null: CanEqual[CharSequence, Null] = CanEqual.derived
given givenCanEqual_CharSequenceNull_Null: CanEqual[CharSequence|Null, Null] = CanEqual.derived
given givenCanEqual_CharSequenceNull_CharSequence: CanEqual[CharSequence|Null, CharSequence] = CanEqual.derived
given givenCanEqual_Null_CharSequence: CanEqual[Null, CharSequence] = CanEqual.derived
given givenCanEqual_Null_CharSequenceNull: CanEqual[Null, CharSequence|Null] = CanEqual.derived
given givenCanEqual_CharSequence_CharSequenceNull: CanEqual[CharSequence, CharSequence|Null] = CanEqual.derived


