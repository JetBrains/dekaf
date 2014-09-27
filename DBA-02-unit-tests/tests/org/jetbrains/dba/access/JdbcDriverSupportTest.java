package org.jetbrains.dba.access;

import org.jetbrains.dba.junit.FineRunner;
import org.jetbrains.dba.junit.TestWithParams;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

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
    final JdbcDriverSupport myDriverSupport = new JdbcDriverSupport();
    String jdbcDriversPath = System.getProperty("jdbc.drivers.path", "jdbc");
    myDriverSupport.addJdbcDir(new File(jdbcDriversPath));
    final Driver driver = myDriverSupport.obtainDriver(driverExample.sampleConnectionString);
    assertThat(driver).isNotNull();
    assertThat(driver.acceptsURL(driverExample.sampleConnectionString)).isTrue();
  }

}
