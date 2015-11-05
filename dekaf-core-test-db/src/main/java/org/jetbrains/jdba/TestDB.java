package org.jetbrains.jdba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.BaseFederatedProvider;
import org.jetbrains.jdba.core.DBFacade;
import org.jetbrains.jdba.core.DBTestHelper;
import org.jetbrains.jdba.core.DBTestHelperFactory;
import org.jetbrains.jdba.intermediate.PrimeIntermediateFederatedProvider;
import org.jetbrains.jdba.util.Providers;

import java.util.Collection;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class TestDB {


  /**
   * Test database.
   */
  @NotNull
  public static DBFacade DB;

  /**
   * Test database helper.
   */
  @NotNull
  public static DBTestHelper TH;


  static {
    DB = prepareDB(false);
    TH = prepareTH(DB);
  }

  public static void reinitDB(boolean pseudoRemote) {
    DB.disconnect();
    DB = prepareDB(pseudoRemote);
    TH = prepareTH(DB);
  }

  private static DBFacade prepareDB(boolean pseudoRemote) {
    BaseFederatedProvider provider = new BaseFederatedProvider();

    if (pseudoRemote) {
      PrimeIntermediateFederatedProvider remoteProvider =
          Providers.loadProvider(PrimeIntermediateFederatedProvider.class);
      provider.initRemotely(remoteProvider);
    }
    else {
      provider.initLocally();
    }

    return provider.openFacade(TestEnvironment.obtainConnectionString(), null, 10, false);
  }

  public static void connect() {
    DB.connect();
  }

  public static void disconnect() {
    DB.disconnect();
  }

  @NotNull
  private static DBTestHelper prepareTH(@NotNull final DBFacade db) {
    Collection<DBTestHelperFactory> helperFactories = Providers.loadAllProviders(DBTestHelperFactory.class);
    for (DBTestHelperFactory helperFactory : helperFactories) {
      if (helperFactory.supportRdbms().contains(db.rdbms())) {
        return helperFactory.createTestHelperFor(db);
      }
    }

    throw new IllegalStateException("No test DB helper factory that can support " + db.rdbms());
  }


  public static void printEnvironmentHeader() {

  }


}
