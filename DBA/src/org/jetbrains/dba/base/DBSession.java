package org.jetbrains.dba.base;

/**
 *
 **/
public interface DBSession extends DBTransaction
{

    /**
     * Performs the given operation in transaction and returns the result.
     * @param operation  operation to perform.
     * @param <R>        type of result.
     * @return           the result.
     */
    public <R> R inTransaction(InTransaction<R> operation);

    /**
     * Performs the given operation in transaction and returns the result.
     * @param operation  operation to perform.
     * @return           the result.
     */
    public void inTransaction(InTransactionNoResult operation);


}
