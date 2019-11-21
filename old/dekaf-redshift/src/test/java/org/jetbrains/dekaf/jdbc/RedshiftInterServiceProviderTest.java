package org.jetbrains.dekaf.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.RedshiftIntermediateProvider.REDSHIFT_CONNECTION_STRING_EXAMPLE;



public class RedshiftInterServiceProviderTest {
  protected RedshiftIntermediateProvider myProvider;

  @Before
  public void setup() {
    myProvider = RedshiftIntermediateProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    Driver driver = DriverManager.getDriver(REDSHIFT_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }
}
