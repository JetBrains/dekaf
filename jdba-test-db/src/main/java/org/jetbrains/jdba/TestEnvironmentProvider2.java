package org.jetbrains.jdba;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static org.jetbrains.jdba.core1.TestEnvironment.getVar;



/**
 * Finds a possible connection string for tests.
 * @author Leonid Bushuev from JetBrains
 */
@Deprecated
final class TestEnvironmentProvider2 {

  private static final String JDBC_DRIVERS_PATH_VAR = "jdbc.drivers.path";
  private static final String JDBC_DRIVERS_DEFAULT_PATH = "jdbc";





  static {
    System.setProperty("java.awt.headless", "true");
  }


  @NotNull
  File obtainJdbcDriversDir() {
    String path = getVar(JDBC_DRIVERS_PATH_VAR, JDBC_DRIVERS_DEFAULT_PATH);
    File dir = new File(path);
    if (!dir.exists()) System.err.printf("JDBC driver path '%s' doesn't exist.\n", dir.getPath());
    else if (!dir.isDirectory()) System.err.printf("JDBC driver path '%s' is not a directory.\n", dir.getPath());
    return dir;
  }



}
