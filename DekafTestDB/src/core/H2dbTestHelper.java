package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.text.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class H2dbTestHelper extends BaseTestHelper<DBFacade> {

  public H2dbTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(H2dbTestHelper.class));
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
