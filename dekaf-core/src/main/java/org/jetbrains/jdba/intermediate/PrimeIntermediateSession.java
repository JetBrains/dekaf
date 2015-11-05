package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBTransactionControl;
import org.jetbrains.jdba.core.ImplementationAccessibleService;
import org.jetbrains.jdba.core.ParameterDef;



/**
 * Portable intermediate DB session.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface PrimeIntermediateSession extends ImplementationAccessibleService, DBTransactionControl {


  //// TRANSACTION CONTROL \\\\

  void beginTransaction();

  void commit();

  void rollback();



  //// SEANCES \\\\

  @NotNull
  PrimeIntermediateSeance openSeance(@NotNull final String statementText,
                                     @Nullable final ParameterDef[] outParameters);



  //// ACTIVITY AND CLOSING \\\\

  /**
   * Checks whether the session is really alive.
   *
   * <p>
   *   If the check is successful, just returns the ping duration.
   * </p>
   * <p>
   *   If the check failed, closes the session and throws an exception.
   * </p>
   *
   * @return ping duration (in milliseconds).
   *
   * @see #isClosed()
   * @see org.jetbrains.jdba.core.DBLeasedSession#ping()
   */
  long ping();

  /**
   * Closes the session.
   *
   * @see #isClosed()
   */
  void close();

  /**
   * Returns whether the session is marked as closed.
   *
   * <p>
   *   This function doesn't perform communications with the real server,
   *   just returns the inner flag.
   * </p>
   *
   * @return <i>true</i> when session is closed.
   *
   * @see #ping()
   * @see #close()
   */
  boolean isClosed();

}
