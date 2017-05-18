package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Redshift;

import java.util.Collections;
import java.util.Set;



public class RedshiftTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_REDSHIFT = Collections.singleton(Redshift.RDBMS);

  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_REDSHIFT;
  }

  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new RedshiftTestHelper(facade);
  }
}
