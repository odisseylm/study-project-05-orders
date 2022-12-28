package com.mvv.bank.log

class Logs {
    companion object {
        private const val safeLength: Int = 512

        @JvmStatic
        fun trimToSafeString(obj: Any?): String? {
            if (obj == null) {
                return null
            }

            val str = obj.toString()
            return if (str.length < safeLength) str
            else str.substring(0, safeLength) + "..."
        }
    }
}

val Any?.safe: String? get() = Logs.trimToSafeString(this)
