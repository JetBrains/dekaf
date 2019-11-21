package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;



public class ExasolTestHelper extends BaseTestHelper<DBFacade> {

  public ExasolTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(ExasolTestHelper.class));
  }

  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1 X");
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
