package org.jetbrains.dekaf.jdbc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
    RedshiftInterServiceProviderTest.class
})
public class RedshiftJdbcIntegrationTests {

  static {
    System.setProperty("java.awt.headless", "true");
  }

}
