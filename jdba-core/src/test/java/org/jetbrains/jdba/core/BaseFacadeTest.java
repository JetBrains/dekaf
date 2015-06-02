package org.jetbrains.jdba.core;

import org.jetbrains.jdba.intermediate.IntegralIntermediateFacade;
import org.jetbrains.jdba.jdbc.JdbcIntermediateFacade;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class BaseFacadeTest extends BaseHyperSonicFacadeTest {

  @Test
  public void get_intermediate_service() {

    IntegralIntermediateFacade intermediateSession =
        myFacade.getSpecificService(
            IntegralIntermediateFacade.class,
            ImplementationAccessibleService.Names.INTERMEDIATE_SERVICE);
    assertThat(intermediateSession).isInstanceOf(JdbcIntermediateFacade.class);

  }

}