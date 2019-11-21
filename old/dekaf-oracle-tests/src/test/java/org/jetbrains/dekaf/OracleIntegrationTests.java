package org.jetbrains.dekaf;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.core.*;
import org.jetbrains.dekaf.jdbc.OracleExceptionRecognizingTest;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            OraclePrimaryTest.class,
                            OracleSessionTest.class,
                            OracleCommandRunnerTest.class,
                            OracleQueryRunnerTest.class,
                            OracleExceptionRecognizingTest.class,
                            OracleTestHelperTest.class
})
public class OracleIntegrationTests {

  @BeforeClass
  public static void setupEnvironment() {
    System.setProperty("user.awt.headless", "true");
    System.setProperty("user.language", "en");
    System.setProperty("user.country", "US");
  }

}
