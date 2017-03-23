package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.Sybase;

import java.util.Collections;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SybaseTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_SYBASE = Collections.singleton(Sybase.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_SYBASE;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new SybaseTestHelper(facade);
  }

}
