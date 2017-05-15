package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.exceptions.DBSessionIsClosedException;
import org.jetbrains.dekaf.exceptions.DBTransactionIsAlreadyStartedException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBTransactionControl {

  /**
   * Begins the transaction.
   *
   * @throws DBTransactionIsAlreadyStartedException when is already in a transaction.
   *
   * @see #commit()
   * @see #rollback()
   * @see #isInTransaction()
   */
  void beginTransaction()
      throws DBTransactionIsAlreadyStartedException;

  /**
   * Determine whether the current session is in a transaction.
   *
   * <p>
   *   This function doesn't call DB server, it just checks
   *   it's own flag.
   * </p>
   *
   * <p>
   *   If the session is closed, it returns <i>false</i> (doesn't throws an exception).
   * </p>
   *
   * @return whether is in a transaction.
   *
   * @see #beginTransaction()
   * @see #commit()
   * @see #rollback()
   */
  boolean isInTransaction();

  /**
   * Commits the current transaction.
   *
   * <p>
   * If not in transaction, just does nothing silently.
   * </p>
   *
   * @see #beginTransaction()
   * @see #rollback()
   *
   * @throws DBSessionIsClosedException
   */
  void commit()
    throws DBSessionIsClosedException;

  /**
   * Roll the current transaction back.
   *
   * <p>
   * If not in transaction or connection is closed, just does nothing silently.
   * </p>
   *
   * @see #beginTransaction()
   * @see #commit()
   */
  void rollback();


}
