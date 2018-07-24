package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;



public class CHouseTestHelper extends BaseTestHelper<DBFacade> {

  public CHouseTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(CHouseTestHelper.class));
  }

  @Override
  public void prepareX1() {
    performCommand("create view if not exists X1 as select 1 X");
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
