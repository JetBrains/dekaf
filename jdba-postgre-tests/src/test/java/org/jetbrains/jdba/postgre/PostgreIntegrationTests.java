package org.jetbrains.jdba.postgre;

/**
 * @author Leonid Bushuev from JetBrains
 */

import org.jetbrains.jdba.junitft.TestSuiteExecutor;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({

             PostgreFacadeTest.class

})
public class PostgreIntegrationTests {

  public static void main(String[] args) {
    TestSuiteExecutor.run(PostgreIntegrationTests.class);
  }

}
