package org.jetbrains.dba.access;

import org.jetbrains.dba.testing.JDBC;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.sql.Driver;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(JDBC.class)
public class JdbcDriverSupportTest {

  @Test
  public void obtainDriver_Postgre() throws SQLException {
    obtainDriver("jdbc:postgresql://localhost/database?user=masha&password=secret");
  }

  @Test
  public void obtainDriver_Oracle_OCI() throws SQLException {
    obtainDriver("jdbc:oracle:thin:username/password@host:1521:ServiceName");
  }

  @Test
  public void obtainDriver_Oracle_thin() throws SQLException {
    obtainDriver("jdbc:oracle:thin:username/password@host:1521:ServiceName");
  }

  @Test
  public void obtainDriver_MSSQL_native() throws SQLException {
    obtainDriver("jdbc:sqlserver://host\\instanceName:1433;databaseName=MyDB;integrationSecurity=true");
  }

  @Test
  public void obtainDriver_MSSQL_jtds() throws SQLException {
    obtainDriver("jdbc:jtds:sqlserver://host:1433/MyDatabase;instanceName=MyInstance");
  }

  @Test
  public void obtainDriver_MySQL() throws SQLException {
    obtainDriver("jdbc:mysql://localhost:3306/Rabbit?user=masha&password=secret");
  }


  private void obtainDriver(String connectionString) throws SQLException {
    final JdbcDriverSupport myDriverSupport = new JdbcDriverSupport();
    myDriverSupport.addJdbcDir(new File("jdbc"));
    final Driver driver = myDriverSupport.obtainDriver(connectionString);
    assertThat(driver).isNotNull();
    assertThat(driver.acceptsURL(connectionString)).isTrue();
  }
}
