package com.mvv.utils

import scala.reflect.ClassTag


//inline
def newInstance[T]()(implicit classTag: ClassTag[T]): T = newJavaInstance(classTag.runtimeClass.asInstanceOf[Class[T]])

private def newJavaInstance[T](javaClass: Class[T]): T = {

    val primaryConstructor = javaClass.getDeclaredConstructor().nn
    if (!primaryConstructor.canAccess(null)) { // for java before java 9 'constructor.isAccessible' should be used
        primaryConstructor.trySetAccessible()
    }
    //@Suppress("UNCHECKED_CAST")
    primaryConstructor.newInstance().asInstanceOf[T]
}
