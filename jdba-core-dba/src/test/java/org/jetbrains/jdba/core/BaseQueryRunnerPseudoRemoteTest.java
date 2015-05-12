package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.intermediate.AdaptIntermediateFacade;
import org.jetbrains.jdba.jdbc.JdbcIntermediateFacade;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseQueryRunnerPseudoRemoteTest extends BaseQueryRunnerTest {

  @NotNull
  protected BaseFacade prepareBaseFacade(@NotNull final JdbcIntermediateFacade jdbcFacade) {
    AdaptIntermediateFacade intermediateFacade = new AdaptIntermediateFacade(jdbcFacade);
    return new BaseFacade(intermediateFacade);
  }

}
