package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ParameterDef;
import org.jetbrains.dekaf.exceptions.DBSessionIsClosedException;

import static org.jetbrains.dekaf.util.Objects.castTo;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateSession implements IntegralIntermediateSession {

  @NotNull
  private final PrimeIntermediateSession myRemoteSession;


  public AdaptIntermediateSession(@NotNull final PrimeIntermediateSession remoteSession) {
    myRemoteSession = remoteSession;
  }


  @Override
  public void beginTransaction() {myRemoteSession.beginTransaction();}

  @Override
  public boolean isInTransaction() {return myRemoteSession.isInTransaction();}

  @Override
  public void commit() {myRemoteSession.commit();}

  @Override
  public void rollback() {myRemoteSession.rollback();}

  @Override
  @NotNull
  public IntegralIntermediateSeance openSeance(@NotNull final String statementText,
                                               @Nullable final ParameterDef[] outParameters) {
    final PrimeIntermediateSeance remoteSeance =
            myRemoteSession.openSeance(statementText, outParameters);
    return new AdaptIntermediateSeance(remoteSeance);
  }

  @Override
  @Nullable
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) {
    if (serviceName.equalsIgnoreCase(Names.INTERMEDIATE_SERVICE)) {
      return castTo(serviceClass, myRemoteSession);
    }
    else {
      return myRemoteSession.getSpecificService(serviceClass, serviceName);
    }
  }


  @Override
  public int ping() {
    if (myRemoteSession.isClosed())
      throw new DBSessionIsClosedException("The intermediate session is closed.");

    return myRemoteSession.ping();
  }

  @Override
  public void close() {myRemoteSession.close();}

  @Override
  public boolean isClosed() {return myRemoteSession.isClosed();}

}
