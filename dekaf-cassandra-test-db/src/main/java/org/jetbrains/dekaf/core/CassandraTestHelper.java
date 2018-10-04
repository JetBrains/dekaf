package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;
import org.jetbrains.dekaf.sql.Rewriters;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.sql.SqlQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.jetbrains.dekaf.core.ImplementationAccessibleService.Names.JDBC_CONNECTION;
import static org.jetbrains.dekaf.core.Layouts.*;



public class CassandraTestHelper extends BaseTestHelper<DBFacade> {

  public CassandraTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(CassandraTestHelper.class));
  }

  @Override
  public void prepareX1() {
    ensureNoTableOrView("X1");
    performScript(scriptum, "X1");
    performCommand(getInsertValuesStatement("X1", 0, 1));
  }

  private String getInsertValuesStatement(String tableName, int firstValue, int amount) {
    StringBuilder builder = new StringBuilder("BEGIN BATCH\n");
    for (int i = firstValue; i < firstValue + amount; i++) {
      builder.append("INSERT INTO ")
             .append(tableName)
             .append(" (X) VALUES (")
             .append(i + 1)
             .append(");\n");
    }
    return builder.append("APPLY BATCH\n").toString();
  }

  @Override
  public void prepareX1000() {
    ensureNoTableOrView("X1000");
    performCommand(scriptum, "X1000");
    performCommand(getInsertValuesStatement("X1000", 0, 1000));
  }

  @Override
  public void prepareX1000000() {
    ensureNoTableOrView("X1000000");
    performCommand(scriptum, "X1000000");
    int step = 1000;
    for (int i = 0; i < 1000; i++) {
      performCommand(getInsertValuesStatement("X1000000", i * step, step));
    }
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    performCommand(scriptum, "CreateDropFunction");
    String currentKeyspace = getCurrentKeyspace();
    setKeyspaceAndPerformMetaQueryCommands(scriptum, currentKeyspace,
                                           "ZapViewsMetaQuery");
    setKeyspaceAndPerformMetaQueryCommands(scriptum, currentKeyspace,
                                           "ZapTablesMetaQuery");
    performCommand(scriptum, "DropDropFunction");
  }

  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    performCommand(scriptum, "CreateDropFunction");
    String currentKeyspace = getCurrentKeyspace();
    // it's not possible to do 'union all' in Cassandra
    setKeyspaceAndPerformMetaQueryCommands(scriptum, currentKeyspace,
                                           "EnsureNoViewMetaQuery",
                                           lower(params));
    setKeyspaceAndPerformMetaQueryCommands(scriptum, currentKeyspace,
                                           "EnsureNoTableMetaQuery",
                                           lower(params));
    performCommand(scriptum, "DropDropFunction");
  }

  private Object[] lower(final Object[] params) {
    Object[] newParams = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      newParams[i] = ((String) params[i]).toLowerCase();
    }
    return newParams;
  }

  private void setKeyspaceAndPerformMetaQueryCommands(@NotNull final Scriptum scriptum,
                                                      @NotNull final String keyspace,
                                                      @NotNull final String metaQueryName,
                                                      final Object... params) {
    final SqlQuery<List<String>> metaQuery =
        scriptum.query(metaQueryName, listOf(oneOf(String.class)))
                .rewrite(Rewriters.replace("keyspace_name_placeholder", keyspace));

    performMetaQueryCommands(metaQuery, params);
  }

  @NotNull
  private String getCurrentKeyspace() {
    String keyspace = db.inSession(new InSession<String>() {
      @Override
      public String run(@NotNull final DBSession session) {
        Connection connection = session.getSpecificService(Connection.class, JDBC_CONNECTION);
        if (connection == null) throw new IllegalArgumentException("Cannot obtain connection");
        try {
          return connection.getCatalog();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
        return null;
      }
    });
    if (keyspace == null) throw new IllegalStateException("No keyspace is selected");
    return keyspace;
  }

  @NotNull
  @Override
  public String fromSingleRowTable() {
    return " from system.local";
  }

  @Override
  public int countTableRows(@NotNull final DBTransaction transaction,
                            @NotNull final String tableName) {
    final String queryText = "select count(*) from " + tableName;

    try {
      return (int) (long) transaction.query(queryText, singleOf(Long.class)).run();
    }
    catch (NoTableOrViewException ntv) {
      return Integer.MIN_VALUE;
    }
  }
}
