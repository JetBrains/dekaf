package org.jetbrains.jdba.oracle;

import oracle.jdbc.OracleDriver;
import org.jetbrains.jdba.core.BaseIntegrationCase;
import org.jetbrains.jdba.core.TestEnvironment;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * Oracle Integration Test Suite.
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

                            OraScriptsTest.class

})
public class OracleIntegrationTests {

  static {
    System.setProperty("java.awt.headless", "true");
  }

  @BeforeClass
  public static void setupDB() {
    TestEnvironment.setup(new OracleServiceFactory(), OracleDriver.class, null);
    BaseIntegrationCase.connectToDB();
  }

}
