@file:JvmName("TestConsoleFun")
package org.jetbrains.dekaf.test.utils


private val printLock = Object()


fun say(text: String) {
    synchronized(printLock) {
        System.out.println(text)
        System.out.flush()
    }
}

fun blame(text: String, exception: Throwable? = null) {
    synchronized(printLock) {
        System.err.println(text)
        exception?.printStackTrace(System.err)
        System.err.flush()
    }
}

fun halt(text: String, errorCode: Int): Nothing {
    blame(text)
    say("FAILED with error code: $errorCode")
    halt(errorCode)
}

fun halt(errorCode: Int): Nothing {
    Thread.sleep(40L)
    System.exit(errorCode)
    throw Exception("Must be halted with exit code: $errorCode")
}


