package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Sqlite;
import org.jetbrains.dekaf.Rdbms;

import java.util.Collections;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SqliteTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_SQLITE = Collections.singleton(Sqlite.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_SQLITE;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new SqliteTestHelper(facade);
  }

}
