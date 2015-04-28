package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * Portable intermediate DB seance.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBInterSeance {

  /**
   * Prepares the specified query.
   *
   * @param queryText
   */
  void prepare(@NotNull String queryText);


  void setInParameters(@NotNull Object[] parameters);


  @NotNull
  <R> DBInterCursor<R> openCursor(int parameterPosition, @NotNull ResultLayout<R> layout);


  //// CLOSE \\\\

  void close();



}
