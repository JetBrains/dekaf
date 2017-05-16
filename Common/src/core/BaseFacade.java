package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBIsNotConnected;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFacade;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateSession;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseFacade implements DBFacade {

  @NotNull
  private final IntegralIntermediateFacade myInterFacade;

  @Nullable
  private ConnectionInfo myConnectionInfo;


  public BaseFacade(@NotNull final IntegralIntermediateFacade interFacade) {
    myInterFacade = interFacade;
  }


  @NotNull
  @Override
  public Rdbms rdbms() {
    return myInterFacade.rdbms();
  }

  @Override
  public synchronized void connect() {
    myInterFacade.connect();
    myConnectionInfo = null;
  }

  @Override
  public synchronized void reconnect() {
    myInterFacade.reconnect();
    myConnectionInfo = null;
  }

  @Override
  public int ping() {
    return Integer.MIN_VALUE;
  }

  @Override
  public synchronized void disconnect() {
    myInterFacade.disconnect();
    myConnectionInfo = null;
  }

  @Override
  public boolean isConnected() {
    return myInterFacade.isConnected();
  }

  @Override
  public ConnectionInfo getConnectionInfo() {
    ConnectionInfo ci = myConnectionInfo;
    if (ci == null) {
      synchronized (this) {
        ci = myConnectionInfo;
        if (ci == null) {
          ci = myInterFacade.getConnectionInfo();
          myConnectionInfo = ci;
        }
      }
    }
    return ci;
  }



  @Override
  public <R> R inTransaction(final InTransaction<? extends R> operation) {
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
  public <R> R inSession(final InSession<? extends R> operation) {
    if (operation == null) throw new IllegalArgumentException("The operation is null");
    if (!isConnected()) throw new DBIsNotConnected("Facade is not connected.");

    final R result;
    final IntegralIntermediateSession interSession = instantiateIntermediateSession();
    try {
      final BaseSession session = new BaseSession(interSession);
      result = operation.run(session);
      session.closeRunners();
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

    final IntegralIntermediateSession interSession = instantiateIntermediateSession();
    try {
      final BaseSession session = new BaseSession(interSession);
      operation.run(session);
      session.closeRunners();
    }
    finally {
      interSession.close();
    }
  }


  @Override
  public synchronized DBLeasedSession leaseSession() {
    final IntegralIntermediateSession interSession = instantiateIntermediateSession();
    final BaseSession baseSession = new BaseSession(interSession);
    return new DBLeasedSessionWrapper(baseSession);
  }


  @NotNull
  protected synchronized IntegralIntermediateSession instantiateIntermediateSession() {
    return myInterFacade.openSession();
  }


  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName)
      throws ClassCastException
  {
    return myInterFacade.getSpecificService(serviceClass, serviceName);
  }

}
