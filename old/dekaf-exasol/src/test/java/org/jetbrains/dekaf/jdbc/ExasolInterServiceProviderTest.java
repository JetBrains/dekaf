package org.jetbrains.dekaf.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.ExasolIntermediateProvider.EXASOL_CONNECTION_STRING_EXAMPLE;



public class ExasolInterServiceProviderTest {

  protected ExasolIntermediateProvider myProvider;

  @Before
  public void setup() {
    myProvider = ExasolIntermediateProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(EXASOL_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

}