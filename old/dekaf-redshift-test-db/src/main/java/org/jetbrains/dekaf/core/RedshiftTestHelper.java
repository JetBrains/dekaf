package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;



public class RedshiftTestHelper extends BaseTestHelper<DBFacade> {

  public RedshiftTestHelper(@NotNull DBFacade db) {
    super(db, Scriptum.of(RedshiftTestHelper.class));
  }

  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1");
  }

  @Override
  public void prepareX1000() {
    performCommand(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    performCommand(scriptum, "X1000000");
  }

}
