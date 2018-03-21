package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ParameterDef;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface IntegralIntermediateSession extends PrimeIntermediateSession {

  @NotNull
  @Override
  IntegralIntermediateSeance openSeance(@NotNull final String statementText,
                                        @Nullable final ParameterDef[] outParameters);

}
