package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.ClickHouse;
import org.jetbrains.dekaf.Rdbms;

import java.util.Collections;
import java.util.Set;



public class CHouseTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_CHOUSE = Collections.singleton(ClickHouse.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_CHOUSE;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new CHouseTestHelper(facade);
  }

}
