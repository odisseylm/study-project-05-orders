package com.mvv.scala.temp.tests.nulls;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@SuppressWarnings("unused")
public class JavaClass {

    @Nullable
    String nullableMethod(@Nullable String param) { return null; }
    
    @Nonnull
    String nonNullMethod(@Nonnull String param) { return "qwerty"; }
}
