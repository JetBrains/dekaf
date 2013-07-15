package org.jetbrains.dba;

import org.testng.annotations.BeforeClass;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class DBTestCase {

  @SuppressWarnings("UnusedDeclaration")
  @BeforeClass
  public void setUpClass() throws Exception {
    Rdbms rdbms = TestDB.ourRdbms; // just to init the class
  }
}
