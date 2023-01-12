package com.mvv.scala.temp.tests.enums;

enum JavaEnum { V1, V2 }

@SuppressWarnings("unused")
public class ScalaEnumsUsageFromJava {

    void test() {
        Object dd = BuySellType.values();
        Object cc = JavaEnum.values();
    }
}
