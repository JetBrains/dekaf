package org.jetbrains.dba.fakedb.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.access.*;
import org.jetbrains.dba.fakedb.FakeDB;
import org.jetbrains.dba.sql.SQL;



/**
 * @author Leonid Bushuev from JetBrains
 */
class FakeFacade implements DBFacade {


  private final FakeSQL mySQL;


  FakeFacade() {
    mySQL = new FakeSQL();
  }



  @NotNull
  @Override
  public Rdbms rdbms() {
    return FakeDB.RDBMS;
  }


  @NotNull
  @Override
  public SQL sql() {
    return mySQL;
  }


  @Override
  public void setSessionsLimit(int sessionsLimit) {
    // TODO implement FakeFacade.setSessionsLimit()
    throw new RuntimeException("Method FakeFacade.setSessionsLimit() is not implemented yet.");
  }


  @Override
  public void connect() {
    // TODO implement FakeFacade.connect()
    throw new RuntimeException("Method FakeFacade.connect() is not implemented yet.");
  }


  @Override
  public void reconnect() {
    // TODO implement FakeFacade.reconnect()
    throw new RuntimeException("Method FakeFacade.reconnect() is not implemented yet.");
  }


  @Override
  public void disconnect() {
    // TODO implement FakeFacade.disconnect()
    throw new RuntimeException("Method FakeFacade.disconnect() is not implemented yet.");
  }


  @Override
  public boolean isConnected() {
    // TODO implement FakeFacade.isConnected()
    throw new RuntimeException("Method FakeFacade.isConnected() is not implemented yet.");
  }


  @Override
  public <R> R inTransaction(InTransaction<R> operation) {
    // TODO implement FakeFacade.inTransaction()
    throw new RuntimeException("Method FakeFacade.inTransaction() is not implemented yet.");
  }


  @Override
  public void inTransaction(InTransactionNoResult operation) {
    // TODO implement FakeFacade.inTransaction()
    throw new RuntimeException("Method FakeFacade.inTransaction() is not implemented yet.");
  }


  @Override
  public <R> R inSession(InSession<R> operation) {
    // TODO implement FakeFacade.inSession()
    throw new RuntimeException("Method FakeFacade.inSession() is not implemented yet.");
  }


  @Override
  public void inSession(InSessionNoResult operation) {
    // TODO implement FakeFacade.inSession()
    throw new RuntimeException("Method FakeFacade.inSession() is not implemented yet.");
  }
}
