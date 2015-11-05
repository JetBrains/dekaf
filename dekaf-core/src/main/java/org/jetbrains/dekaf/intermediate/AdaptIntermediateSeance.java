package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ResultLayout;

import java.util.List;



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
    if (layout.isPortable()) {
      final PrimeIntermediateCursor<R> remoteCursor =
              myRemoteSeance.openCursor(parameterPosition, layout);
      return new AdaptIntermediatePortableCursor<R>(remoteCursor);
    }
    else {
      ResultLayout<List<Object[]>> intermediateLayout =
              layout.makeIntermediateLayout();
      final PrimeIntermediateCursor<List<Object[]>> remoteCursor =
              myRemoteSeance.openCursor(parameterPosition, intermediateLayout);
      return new AdaptIntermediateStructCollectingCursor<R>(remoteCursor, layout);
    }

  }

  @Override
  public void close() {myRemoteSeance.close();}


}
