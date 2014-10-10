package org.jetbrains.dba.access;

import org.jetbrains.dba.Rdbms;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dba.TestDB.FACADE;



public class OraFacadeTest {

  @Test
  public void getRdbms_oracle() {
    assertThat(FACADE.rdbms()).isEqualTo(Rdbms.ORACLE);
  }


  @Test
  public void connect() {
    FACADE.connect();
    assertThat(FACADE.isConnected()).isTrue();
  }


}