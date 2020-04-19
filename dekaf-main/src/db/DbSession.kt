package org.jetbrains.dekaf.main.db


interface DbSession : DbInsideTransaction, AutoCloseable {

    fun beginTransaction(): DbTransaction

    fun ping()

    fun markBroken()

    override fun close()

}



fun<X> DbSession.inTransaction(block: (DbTransaction) -> X): X {
    val transaction = this.beginTransaction()
    try {
        val result = block(transaction)
        transaction.commit()
        return result
    }
    finally {
        transaction.close()
    }
}
