package com.mvv.bank.util

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible


inline fun <reified T: Any> newInstance(): T = internalNewInstance(T::class)

// kotlin does not allow to make this function as 'internal' or 'private' because it is used inside public inline function
fun <T: Any> internalNewInstance(type: KClass<T>): T {
    val primaryConstructor = type.primaryConstructor
    if (primaryConstructor != null && primaryConstructor.parameters.isEmpty()) {
        if (!primaryConstructor.isAccessible) {
            primaryConstructor.isAccessible = true
        }
        return primaryConstructor.call()
    }

    val constructor = type.java.getDeclaredConstructor()
    if (!constructor.canAccess(null)) { // for java before java 9 'constructor.isAccessible' should be used
        constructor.trySetAccessible()
    }
    return constructor.newInstance()
}
