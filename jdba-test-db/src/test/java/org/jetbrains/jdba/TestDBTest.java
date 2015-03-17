package org.jetbrains.jdba;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForEveryRdbms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.TestDB.FACADE;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(ForEveryRdbms.class)
@FixMethodOrder(MethodSorters.JVM)
public class TestDBTest {

  @Test
  public void connect() {
    FACADE.connect();
    assertThat(FACADE.isConnected()).isTrue();
  }


}
