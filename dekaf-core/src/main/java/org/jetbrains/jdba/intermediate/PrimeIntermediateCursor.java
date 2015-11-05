package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface PrimeIntermediateCursor<R> {


  boolean hasRows();

  @NotNull
  String[] getColumnNames();


  void setFetchLimit(int limit);


  R fetch();


  void close();

}
