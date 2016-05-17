package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Rdbms;
import org.jetbrains.dekaf.jdbc.UnknownDatabase;

import java.util.Collections;
import java.util.Set;



/**
 * @author Leonid Bushuev
 **/
public class UnknownDBTestHelperFactory implements DBTestHelperFactory {

  private static final Set<Rdbms> COLLECTION_OF_H2DB = Collections.singleton(UnknownDatabase.RDBMS);


  @NotNull
  @Override
  public Set<Rdbms> supportRdbms() {
    return COLLECTION_OF_H2DB;
  }


  @NotNull
  @Override
  public DBTestHelper createTestHelperFor(@NotNull final DBFacade facade) {
    return new UnknownDBTestHelper(facade);
  }

}
