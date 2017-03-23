package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.intermediate.AdaptIntermediateFacade;
import org.jetbrains.dekaf.jdbc.JdbcIntermediateFacade;



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
