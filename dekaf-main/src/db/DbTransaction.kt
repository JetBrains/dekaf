package org.jetbrains.dekaf.main.db


interface DbTransaction : DbInsideTransaction, AutoCloseable {

    fun commit()

    fun rollback()

    override fun close()

}
