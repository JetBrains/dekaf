package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.exceptions.UnknownDBException;
import org.jetbrains.dekaf.sql.Scriptum;

import java.sql.*;
import java.util.ArrayList;



/**
 * @author Leonid Bushuev
 **/
public class UnknownDBTestHelper extends BaseTestHelper<DBFacade> {

  private boolean initialized;
  private boolean isOracle, isDB2;

  private String fromSingleRowTable = "";

  public UnknownDBTestHelper(@NotNull final DBFacade db) {
    super(db, Scriptum.of(UnknownDBTestHelper.class));
    if (db.isConnected()) initVariables();
  }

  private void initVariablesIfNeeded() {
    if (!initialized) {
      assert db.isConnected() : "Expected that is connected to DB";
      initVariables();
    }
  }

  private void initVariables() {
    ConnectionInfo info = db.getConnectionInfo();
    isOracle = info.rdbmsName.contains("ORACLE");
    isDB2 = info.rdbmsName.startsWith("DB2");
    fromSingleRowTable = isOracle ? " from dual" : isDB2 ? " from sysibm.sysdummy1" : "";
    initialized = true;
  }

  @Override
  public void prepareX1() {
    performCommand("create or replace view X1 as select 1 as X" + fromSingleRowTable);
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

  @NotNull
  @Override
  public String fromSingleRowTable() {
    initVariablesIfNeeded();
    return fromSingleRowTable;
  }


  @Override
  public void ensureNoTableOrView(final String... names) {
    final String[] tableTypes = new String[] {"TABLE","VIEW"};

    final ConnectionInfo connectionInfo = db.getConnectionInfo();
    if (connectionInfo.databaseName == null)
      throw new IllegalStateException("Cannot clean schema when the database name is unknown");
    if (connectionInfo.schemaName == null)
      throw new IllegalStateException("Cannot clean schema when the schema name is unknown");

    db.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        for (String name : names) {
          zapTables(session, connectionInfo, name, tableTypes);
        }

      }
    });
  }

  @Override
  public void zapSchema() {
    final ConnectionInfo connectionInfo = db.getConnectionInfo();
    if (connectionInfo.databaseName == null)
      throw new IllegalStateException("Cannot clean schema when the database name is unknown");
    if (connectionInfo.schemaName == null)
      throw new IllegalStateException("Cannot clean schema when the schema name is unknown");

    db.inSession(new InSessionNoResult() {
      @Override
      public void run(@NotNull final DBSession session) {

        zapTables(session, connectionInfo, "%", null);

      }
    });
  }



  @SuppressWarnings("ThrowFromFinallyBlock")
  private void zapTables(final DBSession session,
                         final ConnectionInfo connectionInfo,
                         final String tableNameMask, final String[] tableTypes) {
    DatabaseMetaData md =
        session.getSpecificService(DatabaseMetaData.class,
                                   ImplementationAccessibleService.Names.JDBC_METADATA);
    if (md == null) throw new RuntimeException("Unable to obtain metadata from unknown database");

    // obtaining tables and list of commands to execute
    final ArrayList<String> commands = new ArrayList<String>();
    try {
      ResultSet tables = md.getTables(connectionInfo.databaseName,
                                      connectionInfo.schemaName,
                                      tableNameMask,
                                      tableTypes);
      try {
        while (tables.next()) {
          String tableName = tables.getString(3);
          String tableType = tables.getString(4);
          if (tableName == null || tableName.isEmpty() || tableType == null || tableType.isEmpty()) continue;
          tableType = tableType.toLowerCase();
          if (tableType.contains("system")) continue;
          String command = "drop " + tableType + ' ' + '"' + tableName + '"';
          commands.add(command);
        }
      }
      finally {
        tables.close();
      }
    }
    catch (SQLException e) {
      throw new UnknownDBException("Unable to list tables of unknown schema: " + e.getMessage(), e, null);
              // TODO try to recognize the error
    }

    // execute commands
    final ArrayList<String> errors = new ArrayList<String>(0);
    final Connection connection =
        session.getSpecificService(Connection.class,
                                   ImplementationAccessibleService.Names.JDBC_CONNECTION);
    assert connection != null;
    try {
      Statement stmt = connection.createStatement();
      try {
        for (String command : commands) {
          try {
            stmt.execute(command);
          }
          catch (SQLException e) {
            String errMessage = "Failed to " + command + ": " + e.getMessage();
            errors.add(errMessage);
          }
        }
      }
      finally {
        stmt.close();
      }
    }
    catch (SQLException e) {
      throw new UnknownDBException("Unable to list tables of unknown schema: " + e.getMessage(), e, null);
      // TODO try to recognize the error
    }

    if (!errors.isEmpty()) {
      StringBuffer b = new StringBuffer(errors.size()*100);
      b.append(errors.size()).append(" errors occurred when attempted to clean the schema:\n");
      for (String error : errors) b.append('\t').append(error).append('\n');
      throw new RuntimeException(b.toString());
    }
  }
}
