package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Oracle;
import org.jetbrains.jdba.Rdbms;

import java.util.Collections;
import java.util.Set;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class OracleTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_ORACLE = Collections.singleton(Oracle.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_ORACLE;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new OracleTestHelper(facade);
  }

}
