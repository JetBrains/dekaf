package org.jetbrains.dekaf.util

import java.io.StringWriter


fun Throwable.getStackTraceText(): CharSequence {
    val w = StringWriter(4096)
    this.printStackTrace(java.io.PrintWriter(w))
    return w.buffer
}
