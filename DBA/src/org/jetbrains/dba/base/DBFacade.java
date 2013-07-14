package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;



/**
 * Primary class that provides with RDBMS-specific services.
 *
 * @see DatabaseRegistry
 */
public interface DBFacade {

  /**
   * The DBMS code that identifiers this kind of the database.
   * A formal string in upper case.
   * For example: ORA, MS, MY, PG.
   *
   * @return
   */
  @NotNull
  public String getDbmsCode();


  @NotNull
  public DBErrorRecognizer getErrorRecognizer();


  public void connect(@NotNull final String jdbcURL);


  public void reconnect();


  public void disconnect();


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
