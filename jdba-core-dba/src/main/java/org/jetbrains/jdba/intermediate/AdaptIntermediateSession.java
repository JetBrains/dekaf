package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ParameterDef;



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
  public void close() {myRemoteSession.close();}
}
