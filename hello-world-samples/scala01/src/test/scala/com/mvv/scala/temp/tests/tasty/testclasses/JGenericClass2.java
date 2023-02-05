package com.mvv.scala.temp.tests.tasty.testclasses;

import java.time.LocalTime;


@SuppressWarnings("unused")
interface JGenericTrait1<A, B extends java.lang.Comparable<B>> {
    default A aVar() { return null; }
    default void aVar$eq(A v) { }
    B bVal();
}

@SuppressWarnings("unused")
interface JGenericTrait2<C> {
    C cVal();
}


@SuppressWarnings("unused")
abstract class JGenericBaseClass1<C> implements JGenericTrait2<C> {
    C baseClass1Var1() { return null; }
    void baseClass1Var1(C v) { }
}

@SuppressWarnings("unused")
public class JGenericClass2 extends JGenericBaseClass1<String> implements JGenericTrait1<Long, LocalTime> {
    private String _class2Var;

    @Override
    public LocalTime bVal() { return null; }

    @Override
    public String cVal() { return null; }
}
