package org.jetbrains.jdba.postgre;

import org.jetbrains.jdba.core1.BaseIntegrationCase;
import org.jetbrains.jdba.core1.CommonBasicIntTest;
import org.jetbrains.jdba.core1.CommonRowsCollectorsTest;
import org.jetbrains.jdba.core1.TestEnvironment;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

                            CommonBasicIntTest.class,
                            CommonRowsCollectorsTest.class,
                            PostgreFacadeTest.class,
                            PostgreBitBoolTest.class

})
public class PostgreIntegrationTests {

  @BeforeClass
  public static void setupDB() {
    TestEnvironment.setup(new PostgreServiceFactory(), org.postgresql.Driver.class, null);
    BaseIntegrationCase.connectToDB();
  }

}
