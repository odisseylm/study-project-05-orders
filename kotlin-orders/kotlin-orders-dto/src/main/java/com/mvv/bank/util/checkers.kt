package com.mvv.bank.util

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties


// It is designed to verify any type (nullable, non-nullable, late-init) without warnings
@Suppress("NOTHING_TO_INLINE")
inline fun <T> checkNotNullAlways(value: T?): T = checkNotNull(value) { "Required value was null." }
@Suppress("NOTHING_TO_INLINE")
inline fun <T> checkNotNullAlways(value: T?, lazyMessage: () -> Any): T = checkNotNull(value, lazyMessage)


fun isInitialized(prop: ()->Any?): Boolean {
    // seems catching UninitializedPropertyAccessException does not work
    val value: Any? = try { prop() } catch (ignore: Exception) { null }
    return (value != null)
}
fun isPropertyInitialized(obj: Any, prop: KProperty<*>): Boolean = isInitialized { prop.getter.call(obj) }


@Suppress("NOTHING_TO_INLINE")
fun <T> checkPropertyInitialized(propName: String, prop: ()->T?) =
    checkInitialized(prop) { "Required property [${propName}] was not initialized." }
fun <T> checkInitialized(prop: ()->T?, lazyMessage: () -> Any) =
    check(isInitialized(prop), lazyMessage)


@Suppress("NOTHING_TO_INLINE")
inline fun <T> checkPropertyInitialized(prop: KProperty<T>) =
    checkPropertyInitialized(prop) { "Required property [${prop.name}] was not initialized." }
// We cannot use method name checkPropertyInitialized() due to 'Overload resolution ambiguity'
fun <T> checkPropertyInitialized(prop: KProperty<T>, lazyMessage: () -> Any) {
    // seems catching UninitializedPropertyAccessException does not work
    val value: T? = try { prop.getter.call() } catch (ignore: Exception) { null }
    checkNotNull(value, lazyMessage)
}


fun <T : Number> checkId(id: T?, lazyMessage: () -> Any): Unit =
    check(id != null && id.toInt() != 0 && id.toInt() != -1, lazyMessage)
@Suppress("NOTHING_TO_INLINE")
inline fun <T : Number> checkId(id: T?): Unit = checkId(id) { "Id is not set or incorrect [$id]." }


fun checkLateInitPropsAreInitialized(obj: Any) {
    val notInitializedPropNames = obj::class.memberProperties
        .filter { it.isLateinit && !isPropertyInitialized(obj, it) }
        .map { it.name }

    if (notInitializedPropNames.isNotEmpty())
        throw IllegalStateException("The following properties $notInitializedPropNames are not initialized")
}
