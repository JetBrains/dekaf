package org.jetbrains.jdba.core;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForEveryRdbms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.TestDB.FACADE;



@Category(ForEveryRdbms.class)
@FixMethodOrder(MethodSorters.JVM)
public class DBFacadeTest {


  @Test
  public void connect() {
    FACADE.connect();
    assertThat(FACADE.isConnected()).isTrue();
  }


  /*
  @Test
  public void getDriverVersion_isNotZero() {
    Version version = FACADE.getDriverVersion();
    assertThat(version).isGreaterThanOrEqualTo(Version.of(1,0));
  }
  */


}