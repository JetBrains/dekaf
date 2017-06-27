package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.SqlQuery;
import org.jetbrains.dekaf.text.Scriptum;

import static org.jetbrains.dekaf.core.QueryLayouts.layoutExistence;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SybaseTestHelper extends BaseTestHelper<DBFacade> {

  public SybaseTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(SybaseTestHelper.class));
  }


  private final SqlQuery<Boolean> myTableOrViewExistenceQuery =
      getScriptum().query("TableOrViewExistence", layoutExistence());


  private boolean tableExists(@NotNull final String name) {
    return getDb().inSession(session ->
           session.query(myTableOrViewExistenceQuery).withParams(name).run()
    );
  }



  /*
  @Override
  public void prepareX1() {
    performCommand(scriptum, "X1");
  }
  */

  @Override
  public void prepareX1() {
    if (tableExists("X1")) return;
    performScript(getScriptum(), "X1");
  }


  public void prepareX10() {
    if (tableExists("X10")) return;
    performScript(getScriptum(), "X10");
  }


  @Override
  public void prepareX1000() {
    if (tableExists("X1000")) return;
    prepareX10();
    performScript(getScriptum(), "X1000");
  }

  @Override
  public void prepareX1000000() {
    if (tableExists("X1000000")) return;
    prepareX1000();
    performCommand(getScriptum(), "X1000000");
  }


  @Override
  protected void ensureNoTableOrView4(final String[] names) {
    // Unfortunately, Sybase provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(getScriptum(), "EnsureNoForeignKeysMetaQuery", names);
    performMetaQueryCommands(getScriptum(), "EnsureNoTableOrViewMetaQuery", names);
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    // Unfortunately, Sybase provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(getScriptum(), "ZapForeignKeysMetaQuery");
    performMetaQueryCommands(getScriptum(), "ZapSchemaMetaQuery");
  }
}
