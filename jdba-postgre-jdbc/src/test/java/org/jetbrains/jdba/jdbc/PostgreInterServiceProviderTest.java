package org.jetbrains.jdba.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.jdbc.PostgreProvider.POSTGRE_CONNECTION_STRING_EXAMPLE;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreInterServiceProviderTest {

  protected PostgreProvider myProvider;

  @Before
  public void setup() {
    myProvider = PostgreProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(POSTGRE_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

}