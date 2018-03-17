@file:JvmName("DisplayString")

package org.jetbrains.dekaf.expectation



fun Any?.displayString(): String =
        if (this != null)
            when (this) {
                is String     -> this
                is Boolean    -> if (this) "true" else "false"
                is Array<*>   -> this.displayString()
                is ByteArray  -> this.displayString()
                is ShortArray -> this.displayString()
                is IntArray   -> this.displayString()
                is LongArray  -> this.displayString()
                is List<*>    -> this.displayString()
                is Set<*>     -> this.displayString()
                else          -> this.toString()
            }
        else "null"

fun ByteArray?.displayString(): String {
    if (this == null) return "null"
    val n = this.size
    if (n == 0) return "an empty array"
    return this.joinToString(separator = ",", prefix = "[", postfix = "]")
}

fun ShortArray?.displayString(): String {
    if (this == null) return "null"
    val n = this.size
    if (n == 0) return "an empty array"
    return this.joinToString(separator = ",", prefix = "[", postfix = "]")
}

fun IntArray?.displayString(): String {
    if (this == null) return "null"
    val n = this.size
    if (n == 0) return "an empty array"
    return this.joinToString(separator = ",", prefix = "[", postfix = "]")
}

fun LongArray?.displayString(): String {
    if (this == null) return "null"
    val n = this.size
    if (n == 0) return "an empty array"
    return this.joinToString(separator = ",", prefix = "[", postfix = "]")
}

fun Array<*>?.displayString(): String {
    if (this == null) return "null"
    val n = this.size
    if (n == 0) return "an empty array"
    return this.joinToString(separator = ",", prefix = "[", postfix = "]", transform = Any?::displayString)
}

fun List<*>?.displayString(): String {
    if (this == null) return "null"
    val n = this.size
    if (n == 0) return "an empty list"
    return this.joinToString(separator = ",", prefix = "[", postfix = "]", transform = Any?::displayString)
}

fun Set<*>?.displayString(): String {
    if (this == null) return "null"
    val n = this.size
    if (n == 0) return "an empty set"
    return this.joinToString(separator = ",", prefix = "{", postfix = "}", transform = Any?::displayString)
}


val Any.objectReference
    get() = Integer.toHexString(System.identityHashCode(this)) + '@' + this.javaClass.simpleName
