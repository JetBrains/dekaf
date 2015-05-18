package org.jetbrains.jdba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBFacade;
import org.jetbrains.jdba.core.DBFederatedProvider;
import org.jetbrains.jdba.util.Providers;



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
    final DBFederatedProvider federatedProvider =
            Providers.loadProvider(DBFederatedProvider.class);

    DB = federatedProvider.openFacade(TestEnvironment.obtainConnectionString(), null, 10, false);
  }


}
