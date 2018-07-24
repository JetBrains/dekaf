package org.jetbrains.dekaf.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.CHouseIntermediateProvider.CHOUSE_CONNECTION_STRING_EXAMPLE;



public class CHouseInterServiceProviderTest {

  protected CHouseIntermediateProvider myProvider;

  @Before
  public void setup() {
    myProvider = CHouseIntermediateProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(CHOUSE_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

}