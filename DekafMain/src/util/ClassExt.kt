@file:JvmName("ClassExt")
package org.jetbrains.dekaf.util

import java.lang.reflect.Constructor


inline fun <reified I> getClassIfExists(className: String): Class<I>? {
    try {
        val iface = I::class.java
        val clazz = Class.forName(className)
        @Suppress("UNCHECKED_CAST")
        if (iface.isAssignableFrom(clazz)) return clazz as Class<I>
        else throw IllegalArgumentException("Class ${clazz.name} doesn't implement ${iface.name}")
    }
    catch(e: ClassNotFoundException) {
        return null
    }
}


fun <O> Class<out O>.getDefaultConstructor(): Constructor<O> {
    val constructors = this.declaredConstructors
    for (c in constructors) {
        if (c.parameters.isEmpty()) {
            c.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            return c as Constructor<O>
        }
    }
    throw IllegalArgumentException("Class ${this.name} has no default constructor")
}


fun <O> Constructor<out O>.instantiate(): O {
    try {
        return this.newInstance(*kotlin.emptyArray())
    }
    catch(e: Exception) {
        throw IllegalStateException("Failed to create an instance using constructor $this: ${e.message}", e)
    }
}