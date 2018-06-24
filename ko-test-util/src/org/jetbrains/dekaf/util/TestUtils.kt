package org.jetbrains.dekaf.util

import org.jetbrains.dekaf.assertions.fail

fun expectIllegalArgumentException(block: () -> Unit) {
    try {
        block()
    }
    catch (e: IllegalArgumentException) {
        // OK
        return
    }

    fail ("Expected IllegalArgumentException")
}
