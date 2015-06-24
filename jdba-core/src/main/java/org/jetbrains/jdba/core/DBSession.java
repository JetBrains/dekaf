package org.jetbrains.jdba.core;



/**
 * A session with database.
 *
 * <p>
 *   The instance of this session can be borrowed from {@link DBFacade},
 *   or instantiated by {@link DBSessions}.
 * </p>
 *
 * @see DBFacade
 * @see DBTransaction
 * @see DBSessions
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
