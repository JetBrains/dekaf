package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Cassandra;
import org.jetbrains.dekaf.Rdbms;

import java.util.Collections;
import java.util.Set;



public class CassandraTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_CASSANDRA = Collections.singleton(Cassandra.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_CASSANDRA;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new CassandraTestHelper(facade);
  }

}
