package org.jetbrains.jdba;

import org.jetbrains.jdba.core1.TestEnvironment;
import org.jetbrains.jdba.jdbc.JdbcDriverSupport;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Deprecated
public class TestEnvironment2 {

  /**
   * Provides with some options how to connect to a test database.
   */
  public static final TestEnvironmentProvider2 TEP = new TestEnvironmentProvider2();

  /**
   * The current RDBMS.
   */
  public static final Rdbms RDBMS = JdbcDriverSupport.determineRdbms(TestEnvironment.obtainConnectionString());

}
