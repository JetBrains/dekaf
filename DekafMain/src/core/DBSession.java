package org.jetbrains.dekaf.core;



/**
 * A session with database.
 *
 * <p>
 *   The instance of this session can be borrowed from {@link DBFacade}.
 * </p>
 *
 * @see DBFacade
 * @see DBTransaction
 **/
public interface DBSession extends DBTransaction, ImplementationAccessibleService {

  /**
   * Performs the given operation in transaction and returns the result.
   *
   * @param operation operation to perform.
   * @param <R>       type of result.
   * @return the result.
   */
  <R> R inTransaction(InTransaction<R> operation);

  /**
   * Performs the given operation in transaction and returns the result.
   *
   * @param operation operation to perform.
   */
  void inTransaction(InTransactionNoResult operation);

}
