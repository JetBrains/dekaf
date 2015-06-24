package org.jetbrains.jdba.core;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBSessionIsClosedException;
import org.jetbrains.jdba.sql.SqlCommand;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.sql.SqlScript;



/**
 * @author Leonid Bushuev from JetBrains
 */
class DBLeasedSessionWrapper implements DBLeasedSession {

  //// STATE \\\\

  /**
   * Original session. Not null if open and active,
   * or null if closed.
   */
  private DBSession myOriginalSession;


  //// CONSTRUCTOR \\\\

  DBLeasedSessionWrapper(@NotNull final DBSession originalSession) {
    myOriginalSession = originalSession;
  }


  //// OWN METHODS \\\\

  @Override
  public synchronized boolean isClosed() {
    return myOriginalSession == null
        || myOriginalSession instanceof DBLeasedSession && ((DBLeasedSession) myOriginalSession).isClosed();
  }

  @Override
  public synchronized void close() {
    final DBSession theSessionToClose = myOriginalSession;
    myOriginalSession = null;
    if (theSessionToClose instanceof DBLeasedSession) {
      ((DBLeasedSession) theSessionToClose).close();
    }
  }

  @Override
  public long ping() {
    if (isClosed()) throw new DBSessionIsClosedException("The session is closed or returned back.");

    if (myOriginalSession instanceof DBLeasedSession) {
      return ((DBLeasedSession) myOriginalSession).ping();
    }
    else {
      // not supported yet
      return Long.MIN_VALUE;
    }
  }


  //// DELEGATE METHODS \\\\


  @Override
  public synchronized <R> R inTransaction(final InTransaction<R> operation) {
    return myOriginalSession.inTransaction(operation);
  }

  @Override
  public synchronized void inTransaction(final InTransactionNoResult operation) {
    myOriginalSession.inTransaction(operation);
  }

  @Override
  @NotNull
  public synchronized DBCommandRunner command(@NotNull final SqlCommand command) {
    return myOriginalSession.command(command);
  }

  @Override
  @NotNull
  public synchronized DBCommandRunner command(@NotNull final String commandText) {
    return myOriginalSession.command(commandText);
  }

  @Override
  @NotNull
  public synchronized <S> DBQueryRunner<S> query(@NotNull final SqlQuery<S> query) {
    return myOriginalSession.query(query);
  }

  @Override
  @NotNull
  public synchronized <S> DBQueryRunner<S> query(@NotNull final String queryText,
                                    @NotNull final ResultLayout<S> layout) {
    return myOriginalSession.query(queryText, layout);
  }

  @Override
  @NotNull
  public synchronized DBScriptRunner script(@NotNull final SqlScript script) {
    return myOriginalSession.script(script);
  }

  @Override
  @Nullable
  public synchronized <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                               @NotNull @MagicConstant(valuesFromClass = Names.class) final String serviceName)
      throws ClassCastException
  {
    return myOriginalSession.getSpecificService(serviceClass, serviceName);
  }

}
