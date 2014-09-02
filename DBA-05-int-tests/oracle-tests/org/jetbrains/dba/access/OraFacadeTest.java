package org.jetbrains.dba.access;

import org.jetbrains.dba.Rdbms;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dba.TestDB.ourDB;



public class OraFacadeTest {

  @Test
  public void getRdbsm_oracle() {
    assertThat(ourDB.getDbms()).isEqualTo(Rdbms.ORACLE);
  }


  @Test
  public void connect() {
    ourDB.connect();
    assertThat(ourDB.isConnected()).isTrue();
  }


}