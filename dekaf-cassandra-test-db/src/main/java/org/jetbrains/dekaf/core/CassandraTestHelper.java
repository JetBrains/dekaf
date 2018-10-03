package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.sql.Scriptum;

import java.sql.Connection;
import java.sql.SQLException;



public class CassandraTestHelper extends BaseTestHelper<DBFacade> {
  private final Connection connection;

  public CassandraTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(CassandraTestHelper.class));
    connection = db.getSpecificService(Connection.class,
                                       ImplementationAccessibleService.Names.JDBC_CONNECTION);
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
    performMetaQueryCommands(scriptum, "ZapSchemaMetaQuery", getCurrentKeyspace());
    performCommand(scriptum, "DropDropFunction");
  }

  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    performCommand(scriptum, "CreateDropFunction");
    super.ensureNoTableOrView4(prependParam(getCurrentKeyspace(), params));
    performCommand(scriptum, "DropDropFunction");
  }

  @NotNull
  private String getCurrentKeyspace() {
    try {
      String keyspace = connection.getCatalog();
      if (keyspace == null) throw new IllegalStateException("No keyspace is selected");
      return keyspace;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Object[] prependParam(@Nullable String param, @NotNull Object[] params) {
    if (param == null) return params;
    Object[] newParams = new Object[params.length + 1];
    newParams[0] = param;
    System.arraycopy(params, 0, newParams, 1, params.length);
    return newParams;
  }
}
