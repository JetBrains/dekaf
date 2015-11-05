package org.jetbrains.jdba.core;

import org.jetbrains.jdba.intermediate.IntegralIntermediateRdbmsProvider;
import org.jetbrains.jdba.jdbc.BaseInMemoryDBCase;
import org.jetbrains.jdba.jdbc.UnknownDatabaseProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseRdbmsProviderTest extends BaseInMemoryDBCase {

  protected static UnknownDatabaseProvider ourUnknownDatabaseProvider =
          new UnknownDatabaseProvider();


  @Test
  public void connect_directly() {
    final BaseRdbmsProvider provider =
            new BaseRdbmsProvider(ourUnknownDatabaseProvider);

    final BaseFacade facade =
            provider.openFacade(H2_CONNECTION_STRING, null, 1, true);

    assertThat(facade.isConnected()).isTrue();

    facade.disconnect();

    assertThat(facade.isConnected()).isFalse();
  }


  @Test
  public void connect_remotely() {
    final BaseRdbmsProvider provider =
            new BaseRdbmsProvider(ourUnknownDatabaseProvider);

    final BaseFacade facade =
            provider.openFacade(H2_CONNECTION_STRING, null, 1, true);

    assertThat(facade.isConnected()).isTrue();

    facade.disconnect();

    assertThat(facade.isConnected()).isFalse();
  }


  @Test
  public void get_intermediate_service() {
    final BaseRdbmsProvider provider =
            new BaseRdbmsProvider(ourUnknownDatabaseProvider);
    final IntegralIntermediateRdbmsProvider intermediateRdbmsProvider =
        provider.getSpecificService(
            IntegralIntermediateRdbmsProvider.class,
            ImplementationAccessibleService.Names.INTERMEDIATE_SERVICE);
    assertThat(intermediateRdbmsProvider).isSameAs(ourUnknownDatabaseProvider);
  }

}