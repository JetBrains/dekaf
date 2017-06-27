package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.text.Scriptum;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MysqlTestHelper extends BaseTestHelper<DBFacade> {

  public MysqlTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(MysqlTestHelper.class));
    getSchemasNotToZap().add("performance_schema");
    getSchemasNotToZap().add("mysql");
  }


  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1");
  }

  @Override
  public void prepareX1000() {
    performCommand(getScriptum(), "X10");
    performCommand(getScriptum(), "X1000");
  }

  @Override
  public void prepareX1000000() {
    prepareX1000();
    performCommand(getScriptum(), "X1000000");
  }


  @Override
  protected void ensureNoTableOrView4(final String[] names) {
    // Unfortunately, MS SQL provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(getScriptum(), "EnsureNoForeignKeysMetaQuery", names);
    performMetaQueryCommands(getScriptum(), "EnsureNoTableOrViewMetaQuery", names);
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    // Unfortunately, MS SQL provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(getScriptum(), "ZapForeignKeysMetaQuery");
    performMetaQueryCommands(getScriptum(), "ZapSchemaMetaQuery");
  }

}
