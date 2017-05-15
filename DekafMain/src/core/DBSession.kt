package org.jetbrains.dekaf.core


/**
 * A session with database.

 *
 *
 * The instance of this session can be borrowed from [DBFacade].
 *

 * @see DBFacade

 * @see DBTransaction
 */
interface DBSession : DBTransaction, ImplementationAccessibleService {

    /**
     * Performs the given operation in transaction and returns the result.

     * @param operation operation to perform.
     * *
     * @param <R>       type of result.
     * *
     * @return the result.
    </R> */
    fun <R> inTransaction(operation: InTransaction<R>): R

    /**
     * Performs the given operation in transaction and returns the result.

     * @param operation operation to perform.
     */
    fun inTransaction(operation: InTransactionNoResult)

}
