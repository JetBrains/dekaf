package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ParameterDef;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import java.sql.*;
import java.util.Arrays;



public class CassandraIntermediateSession extends JdbcIntermediateSession {

  CassandraIntermediateSession(@Nullable JdbcIntermediateFacade facade,
                               @NotNull DBExceptionRecognizer exceptionRecognizer,
                               @NotNull Connection connection, boolean ownConnection) {
    super(facade, exceptionRecognizer, connection, ownConnection);
  }

  @NotNull
  @Override
  PreparedStatement prepareSimpleStatement(@NotNull final String statementText) throws SQLException {
    return getConnection().prepareStatement(statementText);
  }

  @NotNull
  @Override
  public synchronized JdbcIntermediateSeance openSeance(@NotNull final String statementText,
                                                        @Nullable ParameterDef[] outParameterDefs) {
    if (outParameterDefs != null) {
      System.err.println("Parameters are not supported and will be ignored: " + Arrays.toString(
          outParameterDefs));
    }
    return super.openSeance(statementText, null);
  }

  @NotNull
  @Override
  CallableStatement prepareCallableStatement(@NotNull final String statementText) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void performPing(final Statement statement) throws SQLException {
    final String pingQuery = "SELECT uuid() FROM system.local;";
    statement.execute(pingQuery);
  }
}
