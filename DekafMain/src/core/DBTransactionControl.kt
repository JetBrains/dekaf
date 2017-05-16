package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.exceptions.DBSessionIsClosedException
import org.jetbrains.dekaf.exceptions.DBTransactionIsAlreadyStartedException


/**
 * @author Leonid Bushuev
 */
interface DBTransactionControl {

    /**
     * Begins the transaction.
     *
     * @throws DBTransactionIsAlreadyStartedException when is already in a transaction.
     * 
     * @see .commit
     * @see .rollback
     * @see .isInTransaction
     */
    @Throws(DBTransactionIsAlreadyStartedException::class)
    fun beginTransaction()

    /**
     * Determine whether the current session is in a transaction.
     *
     * This function doesn't call DB server, it just checks
     * it's own flag.
     *
     * If the session is closed, it returns *false* (doesn't throws an exception).
     *
     * @return whether is in a transaction.
     * 
     * @see .beginTransaction
     * @see .commit
     * @see .rollback
     */
    val isInTransaction: Boolean

    /**
     * Commits the current transaction.
     *
     * If not in transaction, just does nothing silently.
     *
     * @see .beginTransaction
     * @see .rollback
     * @throws DBSessionIsClosedException
     */
    @Throws(DBSessionIsClosedException::class)
    fun commit()

    /**
     * Roll the current transaction back.
     *
     * If not in transaction or connection is closed, just does nothing silently.
     *
     * @see .beginTransaction
     * @see .commit
     */
    fun rollback()

}
