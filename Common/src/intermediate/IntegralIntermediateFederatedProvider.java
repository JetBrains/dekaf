package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.Rdbms;

import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface IntegralIntermediateFederatedProvider extends PrimeIntermediateFederatedProvider {

  /**
   * Determines the RDBMS by the given connection string and
   * prepares a facade.
   * @param connectionString      database connection string.
   * @param connectionProperties  additional connection properties.
   * @param connectionsLimit      how many server connections allowed at the same time.
   * @return                      the prepared facade (not connected).
   */
  @NotNull
  IntegralIntermediateFacade openFacade(@NotNull String connectionString,
                                        @Nullable Properties connectionProperties,
                                        int connectionsLimit);


  /**
   * Returns the most applicable RDBMS-specific service provider for the given RDBMS.
   * @param rdbms the RDBMS marker.
   * @return  the found provider, or null if not found.
   */
  @Nullable
  IntegralIntermediateRdbmsProvider getSpecificServiceProvider(@NotNull Rdbms rdbms);

}
