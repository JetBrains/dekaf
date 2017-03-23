package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class AdaptIntermediateCursor<T,RT> implements IntegralIntermediateCursor<T> {

  @NotNull
  protected final PrimeIntermediateCursor<RT> myRemoteCursor;

  public AdaptIntermediateCursor(@NotNull final PrimeIntermediateCursor<RT> remoteCursor) {
    myRemoteCursor = remoteCursor;
  }

  @Override
  public boolean hasRows() {
    return myRemoteCursor.hasRows();
  }

  @Override
  @NotNull
  public String[] getColumnNames() {
    return myRemoteCursor.getColumnNames();
  }

  public void setCollectLimit(final int limit) {
    myRemoteCursor.setCollectLimit(limit);
  }

  @Override
  public void close() {
    myRemoteCursor.close();
  }

}
