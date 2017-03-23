package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Mssql;
import org.jetbrains.dekaf.Rdbms;

import java.util.Collections;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MssqlTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_MSSQL = Collections.singleton(Mssql.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_MSSQL;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new MssqlTestHelper(facade);
  }

}
