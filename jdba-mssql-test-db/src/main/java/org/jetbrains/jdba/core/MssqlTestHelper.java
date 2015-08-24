package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MssqlTestHelper extends BaseTestHelper<DBFacade> {

  public MssqlTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(MssqlTestHelper.class));
    schemasNotToZap.add("sys");
  }


  @Override
  public void prepareX1() {
    performCommand("create view X1 as select 1");
  }

  @Override
  public void prepareX1000() {
    performCommand(scriptum, "X10");
    performCommand(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    performCommand(scriptum, "X1000000");
  }

}
