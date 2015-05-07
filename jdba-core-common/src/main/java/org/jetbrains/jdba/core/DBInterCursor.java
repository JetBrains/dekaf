package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface DBInterCursor<R> {


  boolean hasRows();

  @NotNull
  String[] getColumnNames();


  void setFetchLimit(int limit);


  R fetch();


  void close();

}
