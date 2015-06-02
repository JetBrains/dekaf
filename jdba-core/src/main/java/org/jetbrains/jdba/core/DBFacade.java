package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;



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
   * Performs the given operation in transaction and returns the result.
   *
   * @param operation operation to perform.
   * @param <R>       type of result.
   * @return the result.
   * @see DBFacade#inTransaction
   */
  <R> R inTransaction(InTransaction<R> operation);

  /**
   * Performs the given operation in transaction and returns the result.
   *
   * @param operation operation to perform.
   * @see DBFacade#inTransaction
   */
  void inTransaction(InTransactionNoResult operation);


  <R> R inSession(InSession<R> operation);


  void inSession(InSessionNoResult operation);


}
