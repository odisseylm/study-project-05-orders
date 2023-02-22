package org.mvv.scala.tools.beans.testclasses;

@SuppressWarnings("unused")
public class BaseJavaClass2 extends BaseJavaClass1 implements JavaInterface2, JavaInterface1 {
    public String getPublicProp11() { return ""; }
    public void setPublicProp11(String v) {}

    private String interfaceValue11 = "";
    @Override
    public String getInterfaceValue11() { return interfaceValue11; }

    @Override
    public void setInterfaceValue11(String v) { interfaceValue11 = v; }
}
