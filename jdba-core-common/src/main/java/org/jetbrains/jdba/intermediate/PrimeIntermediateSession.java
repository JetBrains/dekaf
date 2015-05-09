package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ParameterDef;



/**
 * Portable intermediate DB session.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface PrimeIntermediateSession {


  //// TRANSACTION CONTROL \\\\

  void beginTransaction();

  void commit();

  void rollback();



  //// SEANCES \\\\

  @NotNull
  PrimeIntermediateSeance openSeance(@NotNull final String statementText,
                                     @Nullable final ParameterDef[] outParameters);


  //// OTHER \\\\

  <I> I getSpecificService(@NotNull Class<I> serviceInterface, @NotNull String name);


  //// CLOSE \\\\

  void close();


}
