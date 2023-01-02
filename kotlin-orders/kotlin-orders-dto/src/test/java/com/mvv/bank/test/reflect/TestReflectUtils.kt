package com.mvv.bank.test.reflect

import java.lang.reflect.Field

fun setProperty(instance: Any, property: String, value: Any?) =
    setOrInitProperty(instance, property, value, initOnly = false)

fun initProperty(instance: Any, property: String, value: Any?) =
    setOrInitProperty(instance, property, value, initOnly = true)


private fun setOrInitProperty(instance: Any, property: String, value: Any?, initOnly: Boolean) {

    var klass: Class<*>? = instance.javaClass
    var propField: Field?

    do {
        propField = klass!!.declaredFields.asSequence()
            .find { it.name == property }
            ?.apply { if (!canAccess(instance)) trySetAccessible() }
        klass = klass.superclass
    } while (propField == null && klass != null && klass != Any::class.java)

    if (propField == null) throw IllegalArgumentException("Property [${property}] in $instance is not found.")

    if (!initOnly || propField.get(instance) == null)
        propField.set(instance, value)
}
