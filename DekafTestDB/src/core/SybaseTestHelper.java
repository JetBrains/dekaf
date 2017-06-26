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
      scriptum.query("TableOrViewExistence", layoutExistence());


  private boolean tableExists(@NotNull final String name) {
    return db.inSession(session ->
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
    performScript(scriptum, "X1");
  }


  public void prepareX10() {
    if (tableExists("X10")) return;
    performScript(scriptum, "X10");
  }


  @Override
  public void prepareX1000() {
    if (tableExists("X1000")) return;
    prepareX10();
    performScript(scriptum, "X1000");
  }

  @Override
  public void prepareX1000000() {
    if (tableExists("X1000000")) return;
    prepareX1000();
    performCommand(scriptum, "X1000000");
  }


  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    // Unfortunately, Sybase provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(scriptum, "EnsureNoForeignKeysMetaQuery", params);
    performMetaQueryCommands(scriptum, "EnsureNoTableOrViewMetaQuery", params);
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    // Unfortunately, Sybase provides no way to easy drop tables.
    // We have to drop foreign keys first.
    performMetaQueryCommands(scriptum, "ZapForeignKeysMetaQuery");
    performMetaQueryCommands(scriptum, "ZapSchemaMetaQuery");
  }
}
