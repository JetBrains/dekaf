package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;



/**
 * Represents a database.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBFacade extends ImplementationAccessibleService {


  /**
   * The DBMS this facade is applicable/connected to.
   */
  @NotNull
  Rdbms rdbms();


  /**
   * Connects to the database server.
   */
  void connect();

  /**
   * Reconnects from the database server.
   */
  void reconnect();

  /**
   * Disconnects from the database server.
   *
   * <p>
   *   If not connected - does nothing.
   * </p>
   */
  void disconnect();

  /**
   * Checks whether it is connected to the server.
   *
   * <p>
   *   TBD does it really performs a "ping" interaction or just returning a kind of internal status?
   * </p>
   *
   * @return whether is connected.
   */
  boolean isConnected();

  /**
   * Provides a brief connection info.
   * @return a brief connection info.
   */
  ConnectionInfo getConnectionInfo();


  /**
   * Performs the given operation in transaction and returns the result.
   *
   * @param operation operation to perform.
   * @param <R>       type of result.
   * @return the result.
   *
   * @see #inTransaction(InTransactionNoResult)
   * @see #inSession(InSession)
   */
  <R> R inTransaction(InTransaction<R> operation);

  /**
   * Performs the given operation in transaction.
   *
   * @param operation   operation to perform.
   *
   * @see #inTransaction(InTransaction)
   * @see #inSession(InSessionNoResult)
   */
  void inTransaction(InTransactionNoResult operation);


  /**
   * Performs the given operation in a session and returns the result.
   * @param operation  operation to perform.
   * @param <R>        type of result.
   * @return           the result.
   *
   * @see #inSession(InSessionNoResult)
   * @see #inTransaction(InTransaction)
   */
  <R> R inSession(InSession<R> operation);

  /**
   * Performs the given operation in a session.
   * @param operation   operation to perform.
   *
   * @see #inSession(InSession)
   * @see #inTransaction(InTransactionNoResult)
   */
  void inSession(InSessionNoResult operation);


  /**
   * Lease a session.
   *
   * When the session is not needed more, the {@link DBLeasedSession#close()} must be called.
   *
   * @return a leased session.
   */
  DBLeasedSession leaseSession();

}
