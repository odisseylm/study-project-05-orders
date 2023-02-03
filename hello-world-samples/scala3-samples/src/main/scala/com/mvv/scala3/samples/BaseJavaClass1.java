package com.mvv.scala3.samples;


public class BaseJavaClass1 implements JavaInterface1 {
    private String privateField1;
    String packageField1;
    protected String protectedField1;
    public String publicField1;

    private String privateMethod1() { return ""; }
    String packageMethod1() { return ""; }
    protected String protectedMethod1() { return ""; }
    public String publicMethod1() { return ""; }

    private String getPrivateProp1() { return ""; }
    private void setPrivateProp1(String v) {}
    String getPackageProp1() { return ""; }
    void setPackageProp1(String v) {}
    protected String getProtectedProp1() { return ""; }
    protected void setProtectedProp1(String v) {}
    public String getPublicProp1() { return ""; }
    public void setPublicProp1(String v) {}
}
