package com.mvv.bank.util

import com.mvv.bank.log.safe
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


// This class is designed because kotlin does not support 'late init' props with custom getter/setter
class LateInitProperty<T, Owner> (
    value: T? = null,
    val propName: String? = null,

    val changeable: Boolean = true,
    // !!! Message should have exactly ${prev} and ${new} (not short forms like $prev and $new)
    val changeErrorMessage: String = if (propName.isNullOrEmpty())
        "Not allowed to change property (from [\${prev}] to [\${new}])."
        else "Not allowed to change property '$propName' (from [\${prev}] to [\${new}]).",

    val validate:   (new: T, prev: T?)->Unit = {_,_->},
    val preUpdate:  (new: T, prev: T?)->Unit = {_,_->},
    val postUpdate: (new: T, prev: T?)->Unit = {_,_->},
) : ReadWriteProperty<Owner, T> {
    private var internalValue: T? = value
    val asNullableValue: T? get() = internalValue
    val asNonNullableValue: T get() = internalValue!!
    fun set(v: T) {
        val prev = this.internalValue
        validateNonChangeable(v, prev)
        validate(v, prev)
        preUpdate(v, prev)
        internalValue = v
        postUpdate(v, prev)
    }

    private fun validateNonChangeable(new: T, prev: T?) {
        if (!changeable && prev != null && new != prev) {
            val msg = changeErrorMessage
                .replace("\${prev}", prev.safe.toString())
                .replace("\${new}", new.safe.toString())
            throw IllegalStateException(msg)
        }
    }

    // T O D O: add logic to verify value on null only if T is nullable. Is it needed???
    override operator fun getValue(thisRef: Owner, property: KProperty<*>): T {
        return asNullableValue
            ?: throw UninitializedPropertyAccessException("Property [$propName] is not initialized yet.")
    }
    override operator fun setValue(thisRef: Owner, property: KProperty<*>, value: T) = set(value)

    override fun toString(): String = "$internalValue"
    override fun equals(other: Any?): Boolean {
        return ((other is LateInitProperty<*, *>) && other.internalValue == this.internalValue)
                || other == internalValue
    }

    override fun hashCode(): Int = internalValue.hashCode()
}
