package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediatePortableCursor<T> extends AdaptIntermediateCursor<T,T> {


  public AdaptIntermediatePortableCursor(@NotNull final PrimeIntermediateCursor<T> remoteCursor) {
    super(remoteCursor);
  }

  @Override
  public T fetch() {return myRemoteCursor.fetch();}


}
