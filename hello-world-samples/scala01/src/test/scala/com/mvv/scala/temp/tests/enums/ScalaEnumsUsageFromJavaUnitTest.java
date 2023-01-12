package com.mvv.scala.temp.tests.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;


class ScalaEnumsUsageFromJavaUnitTest {

    @Test
    void aa() {
        System.out.println("BuySellType.values: " + Arrays.toString(BuySellType.values()));
        System.out.println("JavaEnum.values: " + Arrays.toString(JavaEnum.values()));
        System.out.println("JavaEnum.values: " + Arrays.toString(JDirection.values()));
    }
}
