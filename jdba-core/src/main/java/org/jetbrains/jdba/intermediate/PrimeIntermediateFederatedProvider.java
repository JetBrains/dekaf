package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;

import java.util.Properties;
import java.util.Set;



/**
 * Provides with facades for different database types.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface PrimeIntermediateFederatedProvider {


  /**
   * RDBMS that are supported by (or registered in) this federated provider.
   * @return set of RDBMS markers.
   */
  @NotNull
  Set<Rdbms> supportedRdbms();


  /**
   * Determines the RDBMS by the given connection string and
   * prepares a facade.
   * @param connectionString      database connection string.
   * @param connectionProperties  additional connection properties.
   * @param connectionsLimit      how many server connections allowed at the same time.
   * @return                      the prepared facade (not connected).
   */
  @NotNull
  PrimeIntermediateFacade openFacade(@NotNull String connectionString,
                                     @Nullable Properties connectionProperties,
                                     int connectionsLimit);


  /**
   * Returns the most applicable RDBMS-specific service provider for the given RDBMS.
   * @param rdbms the RDBMS marker.
   * @return  the found provider, or null if not found.
   */
  @Nullable
  PrimeIntermediateRdbmsProvider getSpecificServiceProvider(@NotNull Rdbms rdbms);

}
