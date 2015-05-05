package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.core.DBInterFacade;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class UnknownDatabaseServiceProviderTest extends BaseHyperSonicCase {

  @Test
  public void openFacade_for_HSQL() {
    String connectionString = "jdbc:hsqldb:mem:mymemdb?user=SA";
    final DBInterFacade facade =
            UnknownDatabaseServiceProvider.INSTANCE.openFacade(connectionString, null, 1);
            // we expect no exceptions here
    facade.connect();
    assertThat(facade.isConnected()).isTrue();
    facade.disconnect();
    assertThat(facade.isConnected()).isFalse();
  }

}