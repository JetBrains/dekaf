package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;



/**
 * Portable intermediate DB session.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface DBInterSession {


  //// TRANSACTION CONTROL \\\\

  void beginTransaction();

  void commit();

  void rollback();



  //// SEANCES \\\\

  @NotNull
  DBInterSeance openSeance(@NotNull final QueryKind queryKind);




  //// CLOSE \\\\

  void close();


}
