package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.util.Version;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.jetbrains.dekaf.core.ImplementationAccessibleService.Names.JDBC_CONNECTION;
import static org.jetbrains.dekaf.core.Layouts.singleOf;



public class CassandraTestHelper extends BaseTestHelper<DBFacade> {

  CassandraTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(CassandraTestHelper.class));
  }

  @Override
  public void prepareX1() {
    ensureNoTableOrView("X1");
    performCommand(scriptum, "X1");
    insertValues("X1", 1, 1);
  }

  private void insertValues(final String tableName, final int firstValue, final int amount) {
    db.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        Connection connection = session.getSpecificService(Connection.class, JDBC_CONNECTION);
        if (connection == null) throw new IllegalArgumentException("Cannot obtain connection");
        try {
          PreparedStatement preparedStatement = connection.prepareStatement("insert into " + tableName + " (X) values (?)");
          for (int i = firstValue; i < firstValue + amount; i++) {
            preparedStatement.setObject(1, i);
            preparedStatement.addBatch();
          }
          preparedStatement.executeBatch();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  public void prepareX1000() {
    ensureNoTableOrView("X1000");
    performCommand(scriptum, "X1000");
    insertValues("X1000", 1, 1000);
  }

  @Override
  public void prepareX1000000() {
    ensureNoTableOrView("X1000000");
    performCommand(scriptum, "X1000000");
    int step = 1000;
    for (int i = 0; i < 1000; i++) {
      insertValues("X1000000", i * step + 1, step);
    }
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    final String currentKeyspace = db.getConnectionInfo().schemaName;
    final Version serverVersion = getVersion();
    db.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        Connection connection = session.getSpecificService(Connection.class, JDBC_CONNECTION);
        if (connection == null) throw new IllegalArgumentException("Cannot obtain connection");
        if (serverVersion.isOrGreater(3, 0)) {
          dropObjects(connection, currentKeyspace, "type", "type_name", "system_schema.types");
          dropObjects(connection, currentKeyspace, "materialized view", "view_name", "system_schema.views");
          dropObjects(connection, currentKeyspace, "table", "table_name", "system_schema.tables");
          dropObjects(connection, currentKeyspace, "index", "index_name", "system_schema.indexes");
          dropObjects(connection, currentKeyspace, "trigger", "trigger_name", "system_schema.triggers");
          dropObjects(connection, currentKeyspace, "aggregate", "aggregate_name", "system_schema.aggregates");
          dropObjects(connection, currentKeyspace, "function", "function_name", "system_schema.functions");
        }
        else {
          dropObjects(connection, currentKeyspace, "type", "type_name", "system.schema_usertypes");
          dropCassandra2Indexes(connection, currentKeyspace);
          dropObjects(connection, currentKeyspace, "table", "columnfamily_name", "system.schema_columnfamilies");
          dropObjects(connection, currentKeyspace, "trigger", "trigger_name", "system.schema_triggers");
          if (serverVersion.isOrGreater(2, 2)) {
            dropObjects(connection, currentKeyspace, "aggregate", "aggregate_name", "system.schema_aggregates");
            dropObjects(connection, currentKeyspace, "function", "function_name", "system.schema_functions");
          }
        }
      }
    });
  }

  private void dropCassandra2Indexes(final Connection connection,
                                     final String currentKeyspace) {

    try {
      Statement statement = connection.createStatement();
      statement.execute("select index_name from system.\"IndexInfo\" where table_name = '" + currentKeyspace + "'");
      for (String name : getObjectNames(statement)) {
        drop("index", name, connection);
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

  }

  private List<String> getObjectNames(final Statement statement) throws SQLException {
    ResultSet resultSet = statement.getResultSet();
    ArrayList<String> objectNames = new ArrayList<String>();
    boolean hasResults = resultSet.next();
    while (hasResults) {
      objectNames.add(resultSet.getString(1));
      hasResults = resultSet.next();
    }
    return objectNames;
  }

  private void dropObjects(final Connection connection,
                           final String currentKeyspace,
                           final String type,
                           final String columnName,
                           final String tableName) {
    try {
      Statement statement = connection.createStatement();
      statement.execute("select " + columnName + " from " + tableName + " where keyspace_name = '" + currentKeyspace + "'");
      for (String name : getObjectNames(statement)) {
        drop(type, name, connection);
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void drop(String type, String name, final Connection connection) throws SQLException {
    connection.createStatement().execute("drop " + type + " if exists " + name);
  }

  private void drop(String type, String name) {
    performCommand("drop " + type + " if exists " + name);
  }

  @Override
  protected void ensureNoTableOrView4(final Object[] params) {
    for (Object param : params) {
      if (param == null || param.toString().isEmpty()) continue;
      if (getVersion().isOrGreater(3, 0)) {
        drop("materialized view", param.toString());
      }
      drop("table", param.toString());
    }
  }

  @NotNull
  private Version getVersion() {
    return db.getConnectionInfo().serverVersion;
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
