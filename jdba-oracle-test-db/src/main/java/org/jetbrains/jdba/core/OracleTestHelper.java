package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class OracleTestHelper extends BaseTestHelper<DBFacade> {

  public OracleTestHelper(@NotNull final DBFacade facade) {
    super(facade, Scriptum.of(OracleTestHelper.class));
  }

}
