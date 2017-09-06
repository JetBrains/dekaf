package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Exasol;
import org.jetbrains.dekaf.Rdbms;

import java.util.Collections;
import java.util.Set;



public class ExasolTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_EXASOL = Collections.singleton(Exasol.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_EXASOL;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new ExasolTestHelper(facade);
  }

}
