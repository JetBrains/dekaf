@file:JvmName("ClassFun")
@file:Suppress("nothing_to_inline")

package org.jetbrains.dekaf.main.util

import kotlin.reflect.KClass


inline infix fun Any?.isInstanceOf(klass: KClass<out Any>) =
        this != null && klass.isInstance(this)

inline infix fun Any?.isInstanceOf(klass: Class<out Any>) =
        this != null && klass.isAssignableFrom(this.javaClass)


infix fun Class<*>.iz(superClass: Class<*>): Boolean = superClass.isAssignableFrom(this)

infix fun Class<*>.iz(superClass: KClass<*>): Boolean = superClass.java.isAssignableFrom(this)



fun guessCommonOf(c1: Class<*>, c2: Class<*>): Class<out Any> =
        when {
            c1 === c2 -> c1
            c1 iz c2 -> c2
            c2 iz c1 -> c1
            c1 iz Number::class && c2 iz Number::class -> Number::class.java
            else -> Any::class.java
        }

fun guessCommonOf(c1: Class<*>, c2: Class<*>, c3: Class<*>): Class<out Any> =
        when {
            c1 === c2 && c2 === c3 -> c2
            c2 iz c1 && c3 iz c1 -> c1
            c1 iz c2 && c3 iz c2 -> c2
            c1 iz c3 && c2 iz c3 -> c3
            c1 iz Number::class && c2 iz Number::class && c3 iz Number::class -> Number::class.java
            else -> Any::class.java
        }
