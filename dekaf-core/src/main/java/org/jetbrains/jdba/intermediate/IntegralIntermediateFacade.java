package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public interface IntegralIntermediateFacade extends PrimeIntermediateFacade {

  @NotNull
  @Override
  IntegralIntermediateSession openSession();

}
