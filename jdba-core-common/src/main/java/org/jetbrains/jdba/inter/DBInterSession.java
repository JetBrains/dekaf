package org.jetbrains.jdba.inter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ParameterDef;



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
  DBInterSeance openSeance(@NotNull final String statementText,
                           @Nullable final ParameterDef[] outParameters);




  //// CLOSE \\\\

  void close();


}
