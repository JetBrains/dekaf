package org.jetbrains.dekaf.core

/**
 * @author Leonid Bushuev
 */
interface DBCommandRunner {

    /**
     * Assigns command parameters.
     * @param params parameters, one for each '?' sign in the command.
     * *
     * @return the query.
     */
    fun withParams(vararg params: Any): DBCommandRunner

    /**
     * Performs the query.
     */
    fun run(): DBCommandRunner
    
}

