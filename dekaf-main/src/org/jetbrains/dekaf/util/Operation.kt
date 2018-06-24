@file:JvmName("Operation")
@file:Suppress("nothing_to_inline")

package org.jetbrains.dekaf.util


/**
 * Does nothing.
 * This function can be useful where the compiler or inspections want a function.
 */
inline fun nop() { /* do nothing just to get rid of stupid compiler and inspection warnings */ }
