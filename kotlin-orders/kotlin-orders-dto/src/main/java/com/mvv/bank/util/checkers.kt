package com.mvv.bank.util

import kotlin.reflect.KProperty


// It is designed to verify any type (nullable, non-nullable, late-init) without warnings
@Suppress("NOTHING_TO_INLINE")
inline fun <T> checkNotNullAlways(value: T?): T = checkNotNull(value) { "Required value was null." }
@Suppress("NOTHING_TO_INLINE")
inline fun <T> checkNotNullAlways(value: T?, lazyMessage: () -> Any): T = checkNotNull(value, lazyMessage)


@Suppress("NOTHING_TO_INLINE")
fun <T> checkInitialized(propName: String, prop: ()->T?) =
    checkInitialized(prop) { "Required property [${propName}] was not initialized." }
fun <T> checkInitialized(prop: ()->T?, lazyMessage: () -> Any) {
    // seems catching UninitializedPropertyAccessException does not work
    val value: T? = try { prop() } catch (ignore: Exception) { null }
    checkNotNull(value, lazyMessage)
}


@Suppress("NOTHING_TO_INLINE")
inline fun <T> checkInitialized(prop: KProperty<T>) =
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
inline fun <T : Number> checkId(id: T?): Unit = checkId(id) { "Id is not set or incorrect $id." }
