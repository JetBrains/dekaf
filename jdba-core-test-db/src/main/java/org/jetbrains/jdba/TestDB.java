package org.jetbrains.jdba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.BaseFederatedProvider;
import org.jetbrains.jdba.core.DBFacade;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class TestDB {


  /**
   * Test database.
   */
  @NotNull
  public static DBFacade DB;


  static {
    BaseFederatedProvider provider = new BaseFederatedProvider();
    provider.initLocally();

    DB = provider.openFacade(TestEnvironment.obtainConnectionString(), null, 10, false);
  }

  public static void connect() {
    DB.connect();
  }

  public static void disconnect() {
    DB.disconnect();
  }


  public static void printEnvironmentHeader() {

  }


}
