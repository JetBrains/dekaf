package org.jetbrains.jdba.intermediate;

import org.jetbrains.jdba.core.Layouts;
import org.jetbrains.jdba.jdbc.BaseHyperSonicCase;
import org.jetbrains.jdba.jdbc.UnknownDatabaseProvider;
import org.junit.Test;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class AdaptIntermediateRdbmsProviderTest extends BaseHyperSonicCase {

  @Test
  public void remote_scenario() throws Exception {

    // the vendor-code side
    // the driver is already registered in BaseHyperSonicCase
    UnknownDatabaseProvider remoteProvider = UnknownDatabaseProvider.INSTANCE;

    // the client side
    AdaptIntermediateRdbmsProvider provider = new AdaptIntermediateRdbmsProvider(remoteProvider);

    // try a basic scenario
    IntegralIntermediateFacade facade =
            provider.openFacade(BaseHyperSonicCase.HSQL_CONNECTION_STRING, null, 1);
    facade.connect();
    IntegralIntermediateSession session =
            facade.openSession();
    IntegralIntermediateSeance seance =
            session.openSeance("select 44 from information_schema.schemata limit 1", null);
    seance.execute();
    IntegralIntermediateCursor<Integer> cursor =
            seance.openCursor(0, Layouts.singleOf(Integer.class));
    cursor.fetch();
    cursor.close();
    seance.close();
    session.close();
    facade.disconnect();
  }

}