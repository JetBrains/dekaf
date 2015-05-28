package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface IntegralIntermediateRdbmsProvider extends PrimeIntermediateRdbmsProvider {

  @NotNull
  @Override
  IntegralIntermediateFacade openFacade(@NotNull String connectionString,
                                        @Nullable Properties connectionProperties,
                                        int connectionsLimit);

}
