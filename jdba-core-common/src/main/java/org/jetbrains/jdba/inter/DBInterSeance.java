package org.jetbrains.jdba.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ResultLayout;



/**
 * Portable intermediate DB seance.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBInterSeance {


  void setInParameters(@NotNull Object[] parameters);

  void execute();

  int getAffectedRowsCount();


  @NotNull
  <R> DBInterCursor<R> openCursor(int parameterPosition, @NotNull ResultLayout<R> layout);


  //// CLOSE \\\\

  void close();



}
