package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.core.DBInterRdbmsServiceProvider;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterFederatedServiceProviderTest {

  private JdbcInterFederatedServiceProvider myFederatedProvider;


  @Before
  public void setup() {
    myFederatedProvider = JdbcInterFederatedServiceProvider.INSTANCE;
  }


  @Test
  public void the_UnknownDatabaseServiceProvider_is_registered() {
    DBInterRdbmsServiceProvider provider = myFederatedProvider.getSpecificServiceProvider(UnknownDatabase.RDBMS);
    assertThat(provider).isNotNull()
                        .isSameAs(UnknownDatabaseServiceProvider.INSTANCE);
  }

  @Test
  public void the_UnknownDatabaseServiceProvider_is_accepted_by_connectionString() {
    myFederatedProvider.openFacade(UnknownDatabaseServiceProviderTest.HSQL_MEM_CONNECTION_STRING, null, 1);
    // expect no exceptions here
  }



}