package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.exceptions.DBIsNotConnected;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateFacade;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateSession;
import org.jetbrains.dekaf.util.Function;

import java.util.function.Consumer;



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
  public <R> R inTransaction(@NotNull final Function<@NotNull DBTransaction, R> operation) {
    return inSession(session -> session.inTransaction(operation));
  }

  @Override
  public void inTransactionDo(@NotNull final Consumer<@NotNull DBTransaction> operation) {
    inSessionDo(session -> session.inTransactionDo(operation));
  }

  @Override
  public <R> R inSession(@NotNull final Function<@NotNull DBSession, R> operation) {
    //noinspection ConstantConditions
    if (operation == null) throw new IllegalArgumentException("The operation is null");
    if (!isConnected()) throw new DBIsNotConnected("Facade is not connected.");

    final R result;
    final IntegralIntermediateSession interSession = instantiateIntermediateSession();
    try {
      final BaseSession session = new BaseSession(interSession);
      result = operation.apply(session);
      session.closeRunners();
    }
    finally {
      interSession.close();
    }
    return result;
  }

  @Override
  public void inSessionDo(@NotNull final Consumer<@NotNull DBSession> operation) {
    //noinspection ConstantConditions
    if (operation == null) return;
    if (!isConnected()) throw new DBIsNotConnected("Facade is not connected.");

    final IntegralIntermediateSession interSession = instantiateIntermediateSession();
    try {
      final BaseSession session = new BaseSession(interSession);
      operation.accept(session);
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
