package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateFacade implements IntegralIntermediateFacade {

  @NotNull
  private final Rdbms myRdbms;

  @NotNull
  private final PrimeIntermediateFacade myRemoteFacade;


  public AdaptIntermediateFacade(@NotNull final PrimeIntermediateFacade remoteFacade) {
    myRemoteFacade = remoteFacade;
    myRdbms = remoteFacade.rdbms();
  }


  @Override
  @NotNull
  public Rdbms rdbms() {
    return myRdbms;
  }

  @Override
  public void connect() {myRemoteFacade.connect();}

  @Override
  public void reconnect() {myRemoteFacade.reconnect();}

  @Override
  public void disconnect() {myRemoteFacade.disconnect();}

  @Override
  public boolean isConnected() {return myRemoteFacade.isConnected();}

  @Override
  @NotNull
  public IntegralIntermediateSession openSession() {
    final PrimeIntermediateSession remoteSession = myRemoteFacade.openSession();
    return new AdaptIntermediateSession(remoteSession);
  }


  @Nullable
  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) throws ClassCastException {
    return myRemoteFacade.getSpecificService(serviceClass, serviceName);
  }
}
