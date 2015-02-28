package org.jetbrains.dba.rdbms.microsoft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.BaseSession;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * MS SQL Server session.
 */
final class MssqlSession extends BaseSession {

  public MssqlSession(@NotNull MssqlFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);

    try {
      connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }
    catch (SQLException sqle) {
      throw recognizeError(sqle, "connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT)");
    }
  }

}
