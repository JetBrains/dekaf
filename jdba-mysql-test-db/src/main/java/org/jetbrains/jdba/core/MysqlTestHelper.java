package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MysqlTestHelper extends BaseTestHelper<DBFacade> {

  public MysqlTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(MysqlTestHelper.class));
    schemasNotToZap.add("performance_schema");
    schemasNotToZap.add("mysql");
  }


  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1");
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
