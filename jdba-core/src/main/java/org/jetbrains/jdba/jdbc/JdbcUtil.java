package org.jetbrains.jdba.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * Some internal functions for working with JDBC.
 * @author Leonid Bushuev from JetBrains
 */
abstract class JdbcUtil {

  static boolean isClosed(final Connection connection) throws SQLException {
    if (connection == null) return true;
    try {
      return connection.isClosed();
    }
    catch (AbstractMethodError ame) {
      return false;
    }
  }

  static boolean isClosed(final Statement statement) throws SQLException {
    if (statement == null) return true;
    try {
      return statement.isClosed();
    }
    catch (AbstractMethodError ame) {
      return false;
    }
  }

  static boolean isClosed(final ResultSet rset) throws SQLException {
    if (rset == null) return true;
    try {
      return rset.isClosed();
    }
    catch (AbstractMethodError ame) {
      return false;
    }
  }


  static void close(final Connection connection) {
    try {
      if (isClosed(connection)) return;
      connection.close();
    }
    catch (Exception e) {
      printCloseException(e, connection.getClass());
    }
  }

  static void close(final Statement statement) {
    try {
      if (!isClosed(statement)) {
        statement.close();
      }
    }
    catch (Exception e) {
      printCloseException(e, statement.getClass());
    }
  }

  static void close(final ResultSet rset) {
    try {
      if (!isClosed(rset)) {
        rset.close();
      }
    }
    catch (Exception e) {
      printCloseException(e, rset.getClass());
    }
  }


  static void printCloseException(Exception e, Class serviceClass) {
    final String operation =
        "close JDBC object ("+serviceClass.getCanonicalName()+")";
    printOperationException(e, operation);
  }

  static void printOperationException(final Exception e, final String operation) {
    final String message;
    if (e instanceof SQLException) {
      SQLException sqle = (SQLException) e;
      message = String.format(
          "JDBC ERROR: " +
          "Failed to " + operation + ". SQL exception class (%s) encountered, " +
          "with SQL state %s, error code %d and message: %s",
          sqle.getClass().getName(),
          sqle.getSQLState(),
          sqle.getErrorCode(),
          sqle.getMessage());
    }
    else {
      message = String.format(
          "JDBC ERROR: " +
          "Failed to " + operation + ". Exception class (%s) encountered with message: %s",
          e.getClass().getName(),
          e.getMessage());
    }

    System.err.println(message);
  }


}
