package org.jetbrains.dekaf.jdbc;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseInMemoryDBCase {

  protected static final String H2_CONNECTION_STRING = "jdbc:h2:mem:test";
  protected static final String H2_JDBC_DRIVER_CLASS_NAME = "org.h2.Driver";

  protected static Driver ourH2Driver;


  @BeforeClass
  public static void instantiateDriver() {
    System.setProperty("java.awt.headless", "true");

    try {
      //noinspection unchecked
      Class<Driver> driverClass = (Class<Driver>) Class.forName(H2_JDBC_DRIVER_CLASS_NAME);
      ourH2Driver = driverClass.newInstance();
      DriverManager.registerDriver(ourH2Driver);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AfterClass
  public static void deregisterDriver() throws SQLException {
    assert ourH2Driver != null;
    DriverManager.deregisterDriver(ourH2Driver);
  }


  protected static Connection obtainConnection() {
    assert ourH2Driver != null;
    final Connection connection;
    try {
      connection = ourH2Driver.connect(H2_CONNECTION_STRING, new Properties());
      connection.setAutoCommit(true);
    }
    catch (SQLException e) {
      throw new RuntimeException("Failed to get an HSQL connection: "+e.getMessage(), e);
    }
    return connection;
  }


}
