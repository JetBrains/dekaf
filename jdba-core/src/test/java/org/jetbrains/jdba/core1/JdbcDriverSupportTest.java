package org.jetbrains.jdba.core1;

import org.jetbrains.jdba.jdbc.JdbcDriverSupport;
import org.jetbrains.jdba.junitft.FineRunner;
import org.jetbrains.jdba.junitft.TestWithParams;
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
    File jdbcDriversDir = getJdbcDriversDir();
    final JdbcDriverSupport myDriverSupport = new JdbcDriverSupport();
    myDriverSupport.addJdbcDir(jdbcDriversDir);
    final Driver driver = myDriverSupport.obtainDriver(driverExample.sampleConnectionString);
    assertThat(driver).isNotNull();
    assertThat(driver.acceptsURL(driverExample.sampleConnectionString)).isTrue();
  }


}
