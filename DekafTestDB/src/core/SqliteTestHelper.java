package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.text.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SqliteTestHelper extends BaseTestHelper<DBFacade> {

  public SqliteTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(SqliteTestHelper.class));
  }

  @Override
  public void prepareX1() {
    performCommand("create view X1 as select 1");
  }

  @Override
  public void prepareX1000() {
    performCommand(getScriptum(), "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    performCommand(getScriptum(), "X1000000");
  }
}
