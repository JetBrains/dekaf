package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;



/**
 * Represents a database.
 *
 * @see DBProvider
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBFacade {

  /**
   * The DBMS code that identifiers this kind of the database.
   * A formal string in upper case.
   * For example: ORA, MS, MY, PG.
   *
   * @return DBMS code.
   */
  @NotNull
  public Rdbms getDbms();

  /**
   * Connects to the database server.
   */
  public void connect();

  /**
   * Reconnects from the database server.
   */
  public void reconnect();

  /**
   * Disconnects from the database server.
   *
   * <p>
   *   If not connected - does nothing.
   * </p>
   */
  public void disconnect();

  /**
   * Checks whether it is connected to the server.
   *
   * <p>
   *   TBD does it really performs a "ping" interaction or just returning a kind of internal status?
   * </p>
   *
   * @return whether is connected.
   */
  public boolean isConnected();


  /**
   * Performs the given operation in transaction and returns the result.
   *
   * @param operation operation to perform.
   * @param <R>       type of result.
   * @return the result.
   * @see DBFacade#inTransaction
   */
  public <R> R inTransaction(InTransaction<R> operation);

  /**
   * Performs the given operation in transaction and returns the result.
   *
   * @param operation operation to perform.
   * @see DBFacade#inTransaction
   */
  public void inTransaction(InTransactionNoResult operation);


  public <R> R inSession(InSession<R> operation);


  public void inSession(InSessionNoResult operation);
}
