package org.jetbrains.dba;

import org.jetbrains.dba.access.DBFacade;
import org.testng.Assert;
import org.testng.annotations.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */

public class TestDBTest {

  @Test
  public void provide_something() {
    final DBFacade facade = TestDB.provide();
    Assert.assertNotNull(facade);
  }

}
