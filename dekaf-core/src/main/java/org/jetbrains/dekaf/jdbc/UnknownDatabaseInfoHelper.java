package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;



public class UnknownDatabaseInfoHelper {

  static UnknownDatabaseInfo obtainDatabaseInfo(JdbcIntermediateFacade facade) {
    JdbcIntermediateSession session = facade.openSession();
    try {
      Connection connection = session.getConnection();
      try {
        return obtainDatabaseInfo(connection);
      }
      catch (SQLException e) {
        throw session.recognizeException(e);
      }
    }
    finally {
      session.close();
    }
  }

  static UnknownDatabaseInfo obtainDatabaseInfo(Connection connection) throws SQLException {
    DatabaseMetaData md = connection.getMetaData();
    String databaseProductName = md.getDatabaseProductName();
    return databaseProductName != null
        ? obtainDatabaseInfo(databaseProductName)
        : makeDefaultUnknownInfo();
  }

  private static UnknownDatabaseInfo obtainDatabaseInfo(@NotNull String rdbmsName) {
    UnknownDatabaseInfo info = new UnknownDatabaseInfo();

    info.isOracle = rdbmsName.startsWith("Oracle");
    info.isDB2    = rdbmsName.startsWith("DB2");
    info.isHsql   = rdbmsName.startsWith("HSQL");
    info.isDerby  = rdbmsName.contains("Derby");

    info.fromSingleRowTable =
        info.isOracle               ? " from dual" :
        info.isDB2 || info.isDerby  ? " from sysibm.sysdummy1" :
        info.isHsql                 ? " from information_schema.schemata limit 1" :
        "";

    return info;
  }

  private static UnknownDatabaseInfo makeDefaultUnknownInfo() {
    return new UnknownDatabaseInfo();
  }

}
