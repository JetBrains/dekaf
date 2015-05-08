package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.exceptions.DBIsNotConnected;
import org.jetbrains.jdba.intermediate.IntegralIntermediateFacade;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSession;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseFacade implements DBFacade {

  @NotNull
  private final IntegralIntermediateFacade myInterFacade;

  BaseFacade(@NotNull final IntegralIntermediateFacade interFacade) {
    myInterFacade = interFacade;
  }


  @NotNull
  @Override
  public Rdbms rdbms() {
    return myInterFacade.rdbms();
  }

  @Override
  public void connect() {
    myInterFacade.connect();
  }

  @Override
  public void reconnect() {
    myInterFacade.reconnect();
  }

  @Override
  public void disconnect() {
    myInterFacade.disconnect();
  }

  @Override
  public boolean isConnected() {
    return myInterFacade.isConnected();
  }

  @Override
  public <R> R inTransaction(final InTransaction<R> operation) {
    return inSession(new InSession<R>() {
      @Override
      public R run(@NotNull final DBSession session) {

        return session.inTransaction(operation);

      }
    });
  }

  @Override
  public void inTransaction(final InTransactionNoResult operation) {
    inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        session.inTransaction(operation);

      }
    });
  }

  @Override
  public <R> R inSession(final InSession<R> operation) {
    if (operation == null) throw new IllegalArgumentException("The operation is null");
    if (!isConnected()) throw new DBIsNotConnected("Facade is not connected.");

    final R result;
    final IntegralIntermediateSession interSession = instantiateSession();
    try {
      final BaseSession session = new BaseSession(interSession);
      result = operation.run(session);
    }
    finally {
      interSession.close();
    }
    return result;
  }

  @Override
  public void inSession(final InSessionNoResult operation) {
    if (operation == null) return;
    if (!isConnected()) throw new DBIsNotConnected("Facade is not connected.");

    final IntegralIntermediateSession interSession = instantiateSession();
    try {
      final BaseSession session = new BaseSession(interSession);
      operation.run(session);
    }
    finally {
      interSession.close();
    }
  }

  @NotNull
  protected IntegralIntermediateSession instantiateSession() {
    return myInterFacade.openSession();
  }

}
