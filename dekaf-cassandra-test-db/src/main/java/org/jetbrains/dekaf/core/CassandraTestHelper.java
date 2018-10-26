package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;
import org.jetbrains.dekaf.sql.Scriptum;
import org.jetbrains.dekaf.util.Version;

import java.sql.*;
import java.util.ArrayList;

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

  private void useConnection(final ConnectionConsumer consumer) {
    db.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {
        Connection connection = session.getSpecificService(Connection.class, JDBC_CONNECTION);
        if (connection == null) throw new IllegalArgumentException("Cannot obtain connection");
        try {
          consumer.consume(connection);
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override
  protected void zapSchemaInternally(final ConnectionInfo connectionInfo) {
    final String currentKeyspace = getCurrentKeyspace();
    Version serverVersion = getVersion();
    if (serverVersion.isOrGreater(3, 0)) {
      dropObjects(currentKeyspace, "type_name", "system_schema.types", "type");
      dropObjects(currentKeyspace, "view_name", "system_schema.views", "materialized view");
      dropObjects(currentKeyspace, "table_name", "system_schema.tables", "table");
      dropObjects(currentKeyspace, "index_name", "system_schema.indexes", "index");
      dropObjects(currentKeyspace, "trigger_name", "system_schema.triggers", "trigger");
      dropObjects(currentKeyspace, "aggregate_name", "system_schema.aggregates", "aggregate");
      dropObjects(currentKeyspace, "function_name", "system_schema.functions", "function");
    }
    else {
      dropObjects(currentKeyspace, "type_name", "system.schema_usertypes", "type");
      dropCassandra2Indexes(currentKeyspace);
      dropObjects(currentKeyspace, "columnfamily_name", "system.schema_columnfamilies", "table");
      dropObjects(currentKeyspace, "trigger_name", "system.schema_triggers", "trigger");
      if (serverVersion.isOrGreater(2, 2)) {
        dropObjects(currentKeyspace, "aggregate_name", "system.schema_aggregates", "aggregate");
        dropObjects(currentKeyspace, "function_name", "system.schema_functions", "function");
      }
    }
  }

  private void dropCassandra2Indexes(final String currentKeyspace) {
    useConnection(new ConnectionConsumer() {
      @Override
      void consume(final Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("select index_name from system.\"IndexInfo\" where table_name = '" + currentKeyspace + "'");
        String[] objectNames = getObjectNames(statement);
        for (String objectName : objectNames) {
          int dot = objectName.indexOf('.');
          drop("index", objectName.substring(dot + 1));
        }
      }
    });
  }

  private String[] getObjectNames(final Statement statement) throws SQLException {
    ResultSet resultSet = statement.getResultSet();
    ArrayList<String> objectNames = new ArrayList<String>();
    boolean hasResults = resultSet.next();
    while (hasResults) {
      objectNames.add(resultSet.getString(1));
      hasResults = resultSet.next();
    }
    String[] res = new String[objectNames.size()];
    for (int i = 0; i < objectNames.size(); i++) {
      res[i] = objectNames.get(i);
    }
    return res;
  }

  private void dropObjects(final String currentKeyspace,
                           final String columnName,
                           final String tableName,
                           final String type) {
    useConnection(new ConnectionConsumer() {
      @Override
      void consume(final Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("select " + columnName + " from " + tableName + " where keyspace_name = '" + currentKeyspace + "'");
        String[] objectNames = getObjectNames(statement);
        for (String objectName : objectNames) {
          drop(type, objectName);
        }
      }
    });
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
  private Version getVersion() {
    String version = db.inSession(new InSession<String>() {
      @Override
      public String run(@NotNull final DBSession session) {
        Connection connection = session.getSpecificService(Connection.class, JDBC_CONNECTION);
        if (connection == null) throw new IllegalArgumentException("Cannot obtain connection");
        try {
          return connection.getMetaData().getDatabaseProductVersion();
        }
        catch (SQLException e) {
          e.printStackTrace();
        }
        return null;
      }
    });
    if (version == null) throw new IllegalStateException("Version is not defined");
    return Version.of(version);
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

  private static abstract class ConnectionConsumer {
    abstract void consume(Connection connection) throws SQLException;
  }
}
