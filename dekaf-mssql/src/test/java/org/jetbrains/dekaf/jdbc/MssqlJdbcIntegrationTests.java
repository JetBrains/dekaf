package org.jetbrains.dekaf.jdbc;

/**
 * @author Leonid Bushuev from JetBrains
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            MssqlInterServiceProviderTest.class
})
public class MssqlJdbcIntegrationTests {

  static {
    System.setProperty("java.awt.headless", "true");
  }

}
