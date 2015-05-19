package org.jetbrains.jdba;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.BaseFederatedProvider;
import org.jetbrains.jdba.core.DBFacade;
import org.jetbrains.jdba.intermediate.PrimeIntermediateFederatedProvider;
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
    DB = prepareDB(false);
  }

  public static void reinitDB(boolean pseudoRemote) {
    DB.disconnect();
    DB = prepareDB(pseudoRemote);
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


  public static void printEnvironmentHeader() {

  }


}
