package org.jetbrains.jdba.postgre;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreFacadeTest extends PostgreIntegrationCase {

  @Test
  public void connect_basic() {
    myFacade.connect();
    assertThat(myFacade.isConnected()).isTrue();
  }


}
