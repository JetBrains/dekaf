package org.jetbrains.dekaf.core

/**
 * @author Leonid Bushuev
 */
interface DBScriptRunner {

    /**
     * Performs the script - all commands one by one.
     */
    fun run(): DBScriptRunner
}

