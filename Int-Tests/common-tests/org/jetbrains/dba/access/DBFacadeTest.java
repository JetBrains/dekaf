package org.jetbrains.dba.access;

import org.jetbrains.dba.utils.Version;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing.categories.ForEveryRdbms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dba.TestDB.FACADE;



@Category(ForEveryRdbms.class)
public class DBFacadeTest {


  @Test
  public void connect() {
    FACADE.connect();
    assertThat(FACADE.isConnected()).isTrue();
  }


  @Test
  public void getDriverVersion_isNotZero() {
    Version version = FACADE.getDriverVersion();
    assertThat(version).isGreaterThanOrEqualTo(Version.of(1,0));
  }



}