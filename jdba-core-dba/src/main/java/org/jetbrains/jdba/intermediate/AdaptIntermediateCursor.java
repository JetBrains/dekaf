package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateCursor<T> implements IntegralIntermediateCursor<T> {

  @NotNull
  private final PrimeIntermediateCursor<T> myRemoteCursor;


  public AdaptIntermediateCursor(@NotNull final PrimeIntermediateCursor<T> remoteCursor) {
    myRemoteCursor = remoteCursor;
  }

  @Override
  public boolean hasRows() {return myRemoteCursor.hasRows();}

  @Override
  @NotNull
  public String[] getColumnNames() {return myRemoteCursor.getColumnNames();}

  @Override
  public void setFetchLimit(final int limit) {myRemoteCursor.setFetchLimit(limit);}

  @Override
  public T fetch() {return myRemoteCursor.fetch();}

  @Override
  public void close() {myRemoteCursor.close();}


}
