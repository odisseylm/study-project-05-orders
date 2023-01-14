package com.mvv.scala.temp.tests.enums;

enum JavaEnum { V1, V2 }

@SuppressWarnings("all")
public class ScalaEnumsUsageFromJava {

    //@annotation.nowarn
    void test() {
        Object dd = BuySellType.values();
        Object cc = JavaEnum.values();
    }
}
