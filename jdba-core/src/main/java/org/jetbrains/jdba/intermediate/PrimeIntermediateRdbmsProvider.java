package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.ImplementationAccessibleService;

import java.util.Properties;
import java.util.regex.Pattern;



/**
 * Provides with services that are specific for one RDBMS.
 *
 * @author Leonid Bushuev from JetBrains
 */
public interface PrimeIntermediateRdbmsProvider extends ImplementationAccessibleService {

  //// API \\\\

  /**
   * The RDBMS this provider is for.
   * @return the RDBMS marker.
   */
  @NotNull
  Rdbms rdbms();


  /**
   * The pattern for connection string that accepts this provider.
   * @return the pattern.
   */
  @NotNull
  Pattern connectionStringPattern();


  /**
   * Specificity of this provider — in other words,
   * how natively this provider supports the database. Lower values means more specific case (lower — better).
   * @return the specificity.
   * @see #SPECIFICITY_NATIVE
   * @see #SPECIFICITY_INTERMEDIATE
   * @see #SPECIFICITY_UNSPECIFIC
   */
  byte specificity();


  /**
   * Prepares a facade to the specific database.
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
   * Don't use this method, use {@link IntegralIntermediateRdbmsProvider#getExceptionRecognizer} instead.
   * @return  an exception recognizer class.
   */
  @NotNull
  Class<? extends DBExceptionRecognizer> getExceptionRecognizerClass();


  //// CONSTANTS \\\\

  byte SPECIFICITY_NATIVE = 10;
  byte SPECIFICITY_INTERMEDIATE = 50;
  byte SPECIFICITY_UNSPECIFIC = 90;


}
