package org.jetbrains.dekaf.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.MssqlIntermediateProvider.MSSQL_CONNECTION_STRING_EXAMPLE;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MssqlInterServiceProviderTest {

  protected MssqlIntermediateProvider myProvider;

  @Before
  public void setup() {
    myProvider = MssqlIntermediateProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(MSSQL_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

}