package org.jetbrains.dba;

import org.testng.annotations.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Test(groups = "oracle")
public class TestDBTest2 extends DBTestCase {

  @Test
  public void zap_schema() {
    TestDB.zapSchema();
  }

}
