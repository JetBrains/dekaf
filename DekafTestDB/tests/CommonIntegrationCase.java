package org.jetbrains.dekaf;

import org.jetbrains.dekaf.core.DBFacade;
import org.jetbrains.dekaf.core.DBTestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public abstract class CommonIntegrationCase {

  protected static DBFacade DB;
  protected static DBTestHelper TH;


  @BeforeClass
  public static void setupTestDB() {
    System.setProperty("java.awt.headless", "true");
    DB = TestDB.DB;
    TH = TestDB.TH;
  }


  @AfterClass
  public static void disconnectFromTestDB() {
    if (DB != null) {
      TestDB.disconnect();
    }
  }


}
