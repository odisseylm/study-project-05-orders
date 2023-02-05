package com.mvv.scala.temp.tests.tasty.testclasses;

@SuppressWarnings("unused")
public interface JavaInterface1 {
    default String getInterfaceValue1() { return ""; }
    default String methodInterface1() { return ""; }
    String getInterfaceValue11();
    void setInterfaceValue11(String v);
}
