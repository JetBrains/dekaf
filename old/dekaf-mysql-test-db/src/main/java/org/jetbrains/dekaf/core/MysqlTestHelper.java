package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;



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


  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    // Unfortunately, MS SQL provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(scriptum, "EnsureNoForeignKeysMetaQuery", params);
    performMetaQueryCommands(scriptum, "EnsureNoTableOrViewMetaQuery", params);
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    // Unfortunately, MS SQL provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(scriptum, "ZapForeignKeysMetaQuery");
    performMetaQueryCommands(scriptum, "ZapSchemaMetaQuery");
  }

}
