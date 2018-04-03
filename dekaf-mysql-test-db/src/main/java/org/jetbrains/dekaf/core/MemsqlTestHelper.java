package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;



public class MemsqlTestHelper extends BaseTestHelper<DBFacade> {

  public MemsqlTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(MemsqlTestHelper.class));
    schemasNotToZap.add("performance_schema");
    schemasNotToZap.add("mysql");
    schemasNotToZap.add("memsql");
  }


  @Override
  public void prepareX1() {
    ensureNoTableOrView("X1");
    performCommand("create view X1 as select 1");
  }

  @Override
  public void prepareX1000() {
    ensureNoTableOrView("X10", "X1000");
    performCommand(scriptum, "X10");
    performCommand(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    ensureNoTableOrView("X1000000");
    performCommand(scriptum, "X1000000");
  }


  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    performMetaQueryCommands(scriptum, "EnsureNoForeignKeysMetaQuery", params);
    Object[] params2 = new Object[8];
    for (int i = 0; i < 4; ++i) params2[i] = params2[i + 4] = params[i].toString().isEmpty() ? null : params[i];
    performMetaQueryCommandsIterative(scriptum, "EnsureNoTableOrViewMetaQuery", 10, params2);
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    performMetaQueryCommands(scriptum, "ZapForeignKeysMetaQuery");
    performMetaQueryCommandsIterative(scriptum, "ZapSchemaMetaQuery", 10);
  }

}
