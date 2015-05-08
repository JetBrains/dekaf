package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ResultLayout;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateSeance implements IntegralIntermediateSeance {

  @NotNull
  private final PrimeIntermediateSeance myRemoteSeance;


  public AdaptIntermediateSeance(@NotNull final PrimeIntermediateSeance remoteSeance) {
    myRemoteSeance = remoteSeance;
  }


  @Override
  public void setInParameters(@NotNull final Object[] parameters) {myRemoteSeance.setInParameters(parameters);}

  @Override
  public void execute() {myRemoteSeance.execute();}

  @Override
  public int getAffectedRowsCount() {return myRemoteSeance.getAffectedRowsCount();}

  @Override
  @NotNull
  public <R> IntegralIntermediateCursor<R> openCursor(final int parameterPosition,
                                                      @NotNull final ResultLayout<R> layout) {

    final PrimeIntermediateCursor<R> remoteCursor =
            myRemoteSeance.openCursor(parameterPosition, layout);
    return new AdaptIntermediateCursor<R>(remoteCursor);
  }

  @Override
  public void close() {myRemoteSeance.close();}


}
