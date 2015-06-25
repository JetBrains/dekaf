package org.jetbrains.jdba.jdbc;

/**
 * @author Leonid Bushuev from JetBrains
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            H2dbInterServiceProviderTest.class
})
public class H2dbJdbcIntegrationTests {

  static {
    System.setProperty("java.awt.headless", "true");
  }

}
