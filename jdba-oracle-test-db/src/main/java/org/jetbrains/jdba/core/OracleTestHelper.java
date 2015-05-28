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

  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1 as X from dual");
  }

  @Override
  public void prepareX1000() {
    performCommand(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    performCommand(scriptum, "X1000000");
  }
}
