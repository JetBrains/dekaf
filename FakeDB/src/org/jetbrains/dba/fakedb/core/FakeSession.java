package org.jetbrains.dba.fakedb.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.access.*;
import org.jetbrains.dba.fakedb.errors.FakeSessionBusyException;
import org.jetbrains.dba.sql.SQLCommand;
import org.jetbrains.dba.sql.SQLQuery;
import org.jetbrains.dba.sql.SQLScript;



/**
 * @author Leonid Bushuev from JetBrains
 */
class FakeSession implements DBSession {

  @NotNull
  final FakeFacade facade;

  @Nullable
  private volatile FakeTransaction myTransaction;

  @NotNull
  private final Object myTransactionLock = new Object();


  FakeSession(@NotNull final FakeFacade facade) {
    this.facade = facade;
  }


  @NotNull
  private FakeTransaction beginTransaction() {
    while (true) {
      // try to use the slot
      synchronized (myTransactionLock) {
        if (myTransaction == null) {
          FakeTransaction transaction = new FakeTransaction(this);
          myTransaction = transaction;
          return transaction;
        }
      }
      // wait if busy
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException ie) {
        throw new FakeSessionBusyException(ie.getMessage(), ie, null);
      }
    }
  }

  private void endTransaction() {
    synchronized (myTransactionLock) {
      if (myTransaction != null) {
        // complete the work
        myTransaction = null;
      }
    }
  }


  @Override
  public <R> R inTransaction(InTransaction<R> operation) {
    final DBTransaction transaction = beginTransaction();
    try {
      return operation.run(transaction);
    }
    finally {
      endTransaction();
    }
  }


  @Override
  public void inTransaction(InTransactionNoResult operation) {
    final DBTransaction transaction = beginTransaction();
    try {
      operation.run(transaction);
    }
    finally {
      endTransaction();
    }
  }


  @Override
  public DBCommandRunner command(@NotNull SQLCommand command) {
    // TODO implement FakeSession.command()
    throw new RuntimeException("Method FakeSession.command() is not implemented yet.");
  }


  @Override
  public DBCommandRunner command(@NotNull String commandText) {
    // TODO implement FakeSession.command()
    throw new RuntimeException("Method FakeSession.command() is not implemented yet.");
  }


  @Override
  public <S> DBQueryRunner<S> query(@NotNull SQLQuery<S> query) {
    // TODO implement FakeSession.query()
    throw new RuntimeException("Method FakeSession.query() is not implemented yet.");
  }


  @Override
  public DBScriptRunner script(@NotNull SQLScript script) {
    // TODO implement FakeSession.script()
    throw new RuntimeException("Method FakeSession.script() is not implemented yet.");
  }

}
