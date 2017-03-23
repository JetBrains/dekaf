package org.jetbrains.dekaf.core;



import org.jetbrains.dekaf.intermediate.PrimeIntermediateFederatedProvider;
import org.jetbrains.dekaf.jdbc.BaseInMemoryDBCase;
import org.jetbrains.dekaf.jdbc.JdbcIntermediateFederatedProvider;
import org.jetbrains.dekaf.jdbc.UnknownDatabase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
public class BaseFederatedProviderTest extends BaseInMemoryDBCase {

  @Test
  public void intiLocally_supports_unknownDatabase() {
    BaseFederatedProvider provider = new BaseFederatedProvider();
    provider.initLocally();
    assertThat(provider.supportedRdbms()).contains(UnknownDatabase.RDBMS);
  }

  @Test
  public void intiLocally_openFacade_unknownDatabase() {
    BaseFederatedProvider provider = new BaseFederatedProvider();
    provider.initLocally();

    DBFacade facade = provider.openFacade(H2_CONNECTION_STRING, null, 1, true);
    assertThat(facade.isConnected()).isTrue();
    facade.disconnect();
  }

  @Test
  public void intiRemotely_supports_unknownDatabase() {
    PrimeIntermediateFederatedProvider remoteProvider =
            new JdbcIntermediateFederatedProvider();

    BaseFederatedProvider provider = new BaseFederatedProvider();
    provider.initRemotely(remoteProvider);
    assertThat(provider.supportedRdbms()).contains(UnknownDatabase.RDBMS);
  }

  @Test
  public void intiRemotely_openFacade_unknownDatabase() {
    PrimeIntermediateFederatedProvider remoteProvider =
            new JdbcIntermediateFederatedProvider();

    BaseFederatedProvider provider = new BaseFederatedProvider();
    provider.initRemotely(remoteProvider);

    DBFacade facade = provider.openFacade(H2_CONNECTION_STRING, null, 1, true);
    assertThat(facade.isConnected()).isTrue();
    facade.disconnect();
  }

}