package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.intermediate.PrimeIntermediateRdbmsProvider;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateFederatedProviderTest {

  private JdbcIntermediateFederatedProvider myFederatedProvider;


  @Before
  public void setup() {
    myFederatedProvider = new JdbcIntermediateFederatedProvider();
  }


  @Test
  public void the_UnknownDatabaseServiceProvider_is_registered() {
    PrimeIntermediateRdbmsProvider provider = myFederatedProvider.getSpecificServiceProvider(UnknownDatabase.RDBMS);
    assertThat(provider).isNotNull()
                        .isInstanceOf(UnknownDatabaseProvider.class);
  }

  @Test
  public void the_UnknownDatabaseServiceProvider_is_accepted_by_connectionString() {
    myFederatedProvider.openFacade(UnknownDatabaseProviderTest.HSQL_MEM_CONNECTION_STRING, null, 1);
    // expect no exceptions here
  }



}