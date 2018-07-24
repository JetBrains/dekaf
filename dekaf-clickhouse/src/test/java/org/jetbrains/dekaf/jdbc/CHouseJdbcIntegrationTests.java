package org.jetbrains.dekaf.jdbc;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            CHouseInterServiceProviderTest.class
})
public class CHouseJdbcIntegrationTests {

  static {
    System.setProperty("java.awt.headless", "true");
  }

}
