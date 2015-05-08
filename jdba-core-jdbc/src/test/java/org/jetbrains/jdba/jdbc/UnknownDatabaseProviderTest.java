package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.assertions.PatternAssert;
import org.jetbrains.jdba.intermediate.PrimeIntermediateFacade;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class UnknownDatabaseProviderTest extends BaseHyperSonicCase {

  public static final String HSQL_MEM_CONNECTION_STRING = "jdbc:hsqldb:mem:mymemdb?user=SA";

  private UnknownDatabaseProvider myProvider;


  @Before
  public void setup() {
    myProvider = UnknownDatabaseProvider.INSTANCE;
  }


  @Test
  public void accepts_connectionString_HSQL() {
    PatternAssert.assertThat(myProvider.connectionStringPattern()).fits(HSQL_MEM_CONNECTION_STRING);
  }


  @Test
  public void openFacade_for_HSQL() {
    final PrimeIntermediateFacade facade =
            myProvider.openFacade(HSQL_MEM_CONNECTION_STRING, null, 1);
            // we expect no exceptions here

    facade.connect();
    assertThat(facade.isConnected()).isTrue();
    facade.disconnect();
    assertThat(facade.isConnected()).isFalse();
  }

}