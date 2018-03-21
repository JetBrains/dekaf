package org.jetbrains.dekaf.jdbc;

/**
 * @author Leonid Bushuev from JetBrains
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            SybaseInterServiceProviderTest.class
})
public class SybaseJdbcIntegrationTests {

  static {
    System.setProperty("java.awt.headless", "true");
  }

}
