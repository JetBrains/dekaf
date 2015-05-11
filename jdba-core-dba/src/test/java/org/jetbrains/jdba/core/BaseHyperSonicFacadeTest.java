package org.jetbrains.jdba.core;

import org.jetbrains.jdba.intermediate.AdaptIntermediateFacade;
import org.jetbrains.jdba.jdbc.BaseHyperSonicCase;
import org.jetbrains.jdba.jdbc.JdbcIntermediateFacade;
import org.jetbrains.jdba.jdbc.UnknownDatabaseProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseHyperSonicFacadeTest extends BaseHyperSonicCase {



  protected static BaseFacade ourFacade;
  private static JdbcIntermediateFacade ourRemoteFacade;
  private static AdaptIntermediateFacade ourInterFacade;

  @BeforeClass
  public static void setup() {
    ourRemoteFacade = UnknownDatabaseProvider.INSTANCE.openFacade(HSQL_CONNECTION_STRING, null, 1);
    ourInterFacade = new AdaptIntermediateFacade(ourRemoteFacade);
    ourFacade = new BaseFacade(ourInterFacade);
  }

  @Before
  public void connect() {
    ourFacade.connect();
  }

  @After
  public void disconnect() {
    ourFacade.disconnect();
  }

  protected void checkAllAreClosed() {
    assertThat(ourRemoteFacade.countOpenedCursors()).isZero();
    assertThat(ourRemoteFacade.countOpenedSeances()).isZero();
    assertThat(ourRemoteFacade.countOpenedSessions()).isZero();
  }
}
