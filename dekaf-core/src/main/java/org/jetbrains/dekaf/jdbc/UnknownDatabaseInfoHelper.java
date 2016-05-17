package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.core.ConnectionInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;



public class UnknownDatabaseInfoHelper {

  static UnknownDatabaseInfo obtainDatabaseInfo(ConnectionInfo connectionInfo) {
    return obtainDatabaseInfo(connectionInfo.rdbmsName);
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
    info.isOracle = rdbmsName.contains("ORACLE");
    info.isDB2 = rdbmsName.startsWith("DB2");
    info.fromSingleRowTable = info.isOracle ? " from dual" : info.isDB2 ? " from sysibm.sysdummy1" : "";
    return info;
  }

  private static UnknownDatabaseInfo makeDefaultUnknownInfo() {
    return new UnknownDatabaseInfo();
  }

}
