package org.jetbrains.dba;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



public class TestDBTest {


  @Test
  public void detect_rdbms() {
    assertThat(TestDB.ourRdbms).isNotNull();
  }

  @Test
  public void connect() {
    TestDB.ourDB.connect();
    assertThat(TestDB.ourDB.isConnected()).isTrue();
  }

}