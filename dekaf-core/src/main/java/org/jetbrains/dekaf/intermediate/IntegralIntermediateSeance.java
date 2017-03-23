package org.jetbrains.dekaf.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ResultLayout;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface IntegralIntermediateSeance extends PrimeIntermediateSeance {

  @NotNull
  @Override
  <R> IntegralIntermediateCursor<R> openCursor(int parameterPosition, @NotNull ResultLayout<R> layout);

}
