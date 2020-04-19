package org.jetbrains.dekaf.main.db


interface DbSession : AutoCloseable {

    fun ping()
    

    fun markBroken()

    override fun close()

}