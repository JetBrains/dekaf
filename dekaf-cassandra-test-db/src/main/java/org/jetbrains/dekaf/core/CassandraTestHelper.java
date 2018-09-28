package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.sql.Scriptum;



public class CassandraTestHelper extends BaseTestHelper<DBFacade> {

  public CassandraTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(CassandraTestHelper.class));
  }

  @Override
  public void prepareX1() {
    ensureNoTableOrView("X1");
    performScript(scriptum, "X1");
  }

  @Override
  public void prepareX1000() {

  }

  @Override
  public void prepareX1000000() {

  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    performCommand(scriptum, "CreateDropFunction");
    super.zapSchemaInternally(connectionInfo);
    performCommand(scriptum, "DropDropFunction");
  }

  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    performCommand(scriptum, "CreateDropFunction");
    super.ensureNoTableOrView4(params);
    performCommand(scriptum, "DropDropFunction");
  }
}
