package org.jetbrains.dekaf.core;

import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBSessionIsClosedException;
import org.jetbrains.dekaf.sql.SqlCommand;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.jetbrains.dekaf.sql.SqlScript;
import org.jetbrains.dekaf.util.Function;

import java.util.function.Consumer;



/**
 * @author Leonid Bushuev from JetBrains
 */
class DBLeasedSessionWrapper implements DBLeasedSession {

  //// STATE \\\\

  /**
   * Original session. Not null if open and active,
   * or null if closed.
   */
  private DBLeasedSession myOriginalSession;


  //// CONSTRUCTOR \\\\

  DBLeasedSessionWrapper(@NotNull final DBLeasedSession originalSession) {
    myOriginalSession = originalSession;
  }


  private void checkIsNotClosed() {
    if (myOriginalSession == null) throw new DBSessionIsClosedException("The session is already closed or returned back.");
  }


  //// OWN METHODS \\\\

  @Override
  public synchronized boolean isClosed() {
    return myOriginalSession == null || myOriginalSession.isClosed();
  }

  @Override
  public synchronized void close() {
    final DBLeasedSession theSessionToClose = myOriginalSession;
    myOriginalSession = null;
    if (theSessionToClose != null) theSessionToClose.close();
  }

  @Override
  public int ping() {
    checkIsNotClosed();
    return myOriginalSession.ping();
  }



  //// DELEGATE METHODS \\\\


  @Override
  public void beginTransaction() {
    checkIsNotClosed();
    myOriginalSession.beginTransaction();
  }

  @Override
  public boolean isInTransaction() {
    return myOriginalSession != null && myOriginalSession.isInTransaction();
  }

  @Override
  public void commit() {
    checkIsNotClosed();
    myOriginalSession.commit();
  }

  @Override
  public void rollback() {
    if (myOriginalSession == null) return;
    myOriginalSession.rollback();
  }

  @Override
  public <R> R inTransaction(@NotNull final Function<@NotNull DBTransaction, R> operation) {
    checkIsNotClosed();
    return myOriginalSession.inTransaction(operation);
  }

  @Override
  public void inTransactionDo(@NotNull final Consumer<@NotNull DBTransaction> operation) {
    checkIsNotClosed();
    myOriginalSession.inTransactionDo(operation);
  }

  @Override
  @NotNull
  public synchronized DBCommandRunner command(@NotNull final SqlCommand command) {
    checkIsNotClosed();
    return myOriginalSession.command(command);
  }

  @Override
  @NotNull
  public synchronized DBCommandRunner command(@NotNull final String commandText) {
    checkIsNotClosed();
    return myOriginalSession.command(commandText);
  }

  @Override
  @NotNull
  public synchronized <S> DBQueryRunner<S> query(@NotNull final SqlQuery<S> query) {
    checkIsNotClosed();
    return myOriginalSession.query(query);
  }

  @Override
  @NotNull
  public synchronized <S> DBQueryRunner<S> query(@NotNull final String queryText,
                                                 @NotNull final ResultLayout<S> layout) {
    checkIsNotClosed();
    return myOriginalSession.query(queryText, layout);
  }

  @Override
  @NotNull
  public synchronized DBScriptRunner script(@NotNull final SqlScript script) {
    checkIsNotClosed();
    return myOriginalSession.script(script);
  }

  @Override
  @Nullable
  public synchronized <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                               @NotNull @MagicConstant(valuesFromClass = Names.class) final String serviceName)
      throws ClassCastException
  {
    checkIsNotClosed();
    return myOriginalSession.getSpecificService(serviceClass, serviceName);
  }

}
