package org.jetbrains.dba.core;

import org.jetbrains.dba.jdbc.JdbcDriverSupport;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import testing.junit.FineRunner;
import testing.junit.TestWithParams;

import java.io.File;
import java.sql.Driver;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class JdbcDriverSupportTest extends JdbcTestCase {


  @TestWithParams(params = "DRIVER_EXAMPLES")
  public void obtainDriver(DriverExample driverExample) throws SQLException {
    File jdbcDriversDir = getJdbcDriversDir();
    final JdbcDriverSupport myDriverSupport = new JdbcDriverSupport();
    myDriverSupport.addJdbcDir(jdbcDriversDir);
    final Driver driver = myDriverSupport.obtainDriver(driverExample.sampleConnectionString);
    assertThat(driver).isNotNull();
    assertThat(driver.acceptsURL(driverExample.sampleConnectionString)).isTrue();
  }


}
