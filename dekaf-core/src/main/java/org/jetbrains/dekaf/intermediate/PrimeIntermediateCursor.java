package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface PrimeIntermediateCursor<R> {


  boolean hasRows();

  @NotNull
  String[] getColumnNames();


  void setCollectLimit(int limit);


  R fetch();


  void close();

}
