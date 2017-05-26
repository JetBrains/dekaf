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


fun <I> Class<out I>.getDefaultConstructor(): Constructor<I> {
    val constructors = this.declaredConstructors
    for (c in constructors) {
        if (c.parameters.isEmpty()) {
            c.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            return c as Constructor<I>
        }
    }
    throw IllegalArgumentException("Class ${this.name} has no default constructor")
}
