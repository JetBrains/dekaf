package org.jetbrains.dekaf.intermediate;

import org.jetbrains.dekaf.core.Layouts;
import org.jetbrains.dekaf.jdbc.BaseInMemoryDBCase;
import org.jetbrains.dekaf.jdbc.UnknownDatabaseProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateRdbmsProviderTest extends BaseInMemoryDBCase {

  protected static UnknownDatabaseProvider ourUnknownDatabaseProvider =
          new UnknownDatabaseProvider();

  @Test
  public void remote_ping() throws Exception {

    // the vendor-code side
    // the driver is already registered in BaseHyperSonicCase
    UnknownDatabaseProvider remoteProvider = ourUnknownDatabaseProvider;

    // the client side
    AdaptIntermediateRdbmsProvider provider = new AdaptIntermediateRdbmsProvider(remoteProvider);

    // try a basic scenario
    IntegralIntermediateFacade facade =
            provider.openFacade(H2_CONNECTION_STRING, null, 1);
    facade.connect();
    IntegralIntermediateSession session =
            facade.openSession();

    long duration = session.ping();

    assertThat(duration).isGreaterThan(0);

    session.close();
    facade.disconnect();
  }

  @Test
  public void remote_scenario() throws Exception {

    // the vendor-code side
    // the driver is already registered in BaseHyperSonicCase
    UnknownDatabaseProvider remoteProvider = ourUnknownDatabaseProvider;

    // the client side
    AdaptIntermediateRdbmsProvider provider = new AdaptIntermediateRdbmsProvider(remoteProvider);

    // try a basic scenario
    IntegralIntermediateFacade facade =
            provider.openFacade(H2_CONNECTION_STRING, null, 1);
    facade.connect();
    IntegralIntermediateSession session =
            facade.openSession();
    IntegralIntermediateSeance seance =
            session.openSeance("select 44", null);
    seance.execute();
    IntegralIntermediateCursor<Integer> cursor =
            seance.openCursor(0, Layouts.singleOf(Integer.class));
    cursor.fetch();
    cursor.close();
    seance.close();
    session.close();
    facade.disconnect();
  }

  @Test
  public void getExceptionRecognizer_same() {
    AdaptIntermediateRdbmsProvider adaptProvider =
        new AdaptIntermediateRdbmsProvider(ourUnknownDatabaseProvider);

    assertThat(adaptProvider.getExceptionRecognizer()).isSameAs(ourUnknownDatabaseProvider.getExceptionRecognizer());
  }

}