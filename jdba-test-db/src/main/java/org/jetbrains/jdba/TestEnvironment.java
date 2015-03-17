package org.jetbrains.jdba;

import org.jetbrains.jdba.jdbc.JdbcDriverSupport;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class TestEnvironment {

  /**
   * Provides with some options how to connect to a test database.
   */
  public static final TestEnvironmentProvider TEP = new TestEnvironmentProvider();

  /**
   * The current RDBMS.
   */
  public static final Rdbms RDBMS = JdbcDriverSupport.determineRdbms(TEP.obtainConnectionString());

}
