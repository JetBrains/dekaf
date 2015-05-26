package org.jetbrains.jdba.core;

import org.jetbrains.jdba.intermediate.PrimeIntermediateRdbmsProvider;
import org.jetbrains.jdba.jdbc.BaseHyperSonicCase;
import org.jetbrains.jdba.jdbc.JdbcIntermediateRdbmsProvider;
import org.jetbrains.jdba.jdbc.UnknownDatabaseProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseRdbmsProviderTest extends BaseHyperSonicCase {

  protected static UnknownDatabaseProvider ourUnknownDatabaseProvider =
          new UnknownDatabaseProvider();


  @Test
  public void connect_directly() {
    final JdbcIntermediateRdbmsProvider jdbcRdbmsProvider = ourUnknownDatabaseProvider;

    final BaseRdbmsProvider provider =
            new BaseRdbmsProvider(jdbcRdbmsProvider);

    final BaseFacade facade =
            provider.openFacade(HSQL_CONNECTION_STRING, null, 1, true);

    assertThat(facade.isConnected()).isTrue();

    facade.disconnect();

    assertThat(facade.isConnected()).isFalse();
  }


  @Test
  public void connect_remotely() {
    final PrimeIntermediateRdbmsProvider pseudoRemoteProvider = ourUnknownDatabaseProvider;

    final BaseRdbmsProvider provider =
            new BaseRdbmsProvider(pseudoRemoteProvider);

    final BaseFacade facade =
            provider.openFacade(HSQL_CONNECTION_STRING, null, 1, true);

    assertThat(facade.isConnected()).isTrue();

    facade.disconnect();

    assertThat(facade.isConnected()).isFalse();
  }


}