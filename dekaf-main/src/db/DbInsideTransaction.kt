package org.jetbrains.dekaf.main.db


interface DbInsideTransaction {

    /**
     * Performs a simple statement without parameters or result sets.
     */
    fun perform(statementText: String)

}