package org.jetbrains.dekaf.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.MysqlIntermediateProvider.MYSQL_CONNECTION_STRING_EXAMPLE;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class MysqlInterServiceProviderTest {

  protected MysqlIntermediateProvider myProvider;

  @Before
  public void setup() {
    myProvider = MysqlIntermediateProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(MYSQL_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

}