package org.jetbrains.dekaf.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.SybaseIntermediateProvider.SYBASE_JTDS_CONNECTION_STRING_EXAMPLE;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class SybaseInterServiceProviderTest {

  protected SybaseIntermediateProvider myProvider;

  @Before
  public void setup() {
    myProvider = SybaseIntermediateProvider.INSTANCE;
  }

  @Test
  public void driverIsLoaded() throws SQLException {
    final Driver driver = DriverManager.getDriver(SYBASE_JTDS_CONNECTION_STRING_EXAMPLE);
    assertThat(driver).isNotNull();
  }

}