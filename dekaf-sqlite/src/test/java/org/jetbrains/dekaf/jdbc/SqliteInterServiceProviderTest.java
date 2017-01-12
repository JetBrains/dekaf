package org.jetbrains.dekaf.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.SqliteIntermediateProvider.SQLITE_CONNECTION_STRING_EXAMPLE;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SqliteInterServiceProviderTest {

  protected SqliteIntermediateProvider myProvider;

  @Before
  public void setup() {
    myProvider = SqliteIntermediateProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(SQLITE_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

}