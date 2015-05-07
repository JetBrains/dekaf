package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.sql.SQL;



/**
 * Represents a database.
 *
 * @see DBProvider
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBFacade {

  /**
   * The DBMS this facade is applicable/connected to.
   */
  @NotNull
  public Rdbms rdbms();

  /**
   * The SQL dialect that is set up according to the current database.
   *
   * <p>
   *   If the facade was not connected yet,
   *   this dialect is just a default dialect of the current RDBMS and used JDBC drivers.
   *   On the first connect, this dialect can be tuned up to the version or feature set
   *   of the connected database.
   * </p>
   *
   * @return the SQL dialect.
   */
  @NotNull
  public SQL sql();

  /**
   * Specify how many connection to hold at most.
   * @param sessionsLimit the maximum number of connections.
   */
  void setSessionsLimit(int sessionsLimit);

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
