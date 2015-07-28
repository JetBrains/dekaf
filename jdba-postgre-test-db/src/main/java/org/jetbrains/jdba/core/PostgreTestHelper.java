package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class PostgreTestHelper extends BaseTestHelper<DBFacade> {

  public PostgreTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(PostgreTestHelper.class));
    schemasNotToZap.add("pg_catalog");
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
