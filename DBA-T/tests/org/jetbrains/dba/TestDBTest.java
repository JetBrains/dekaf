package org.jetbrains.dba;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dba.TestDB.FACADE;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
public class TestDBTest {

  @Test
  public void connect() {
    FACADE.connect();
    assertThat(FACADE.isConnected()).isTrue();
  }

}
