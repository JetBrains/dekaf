package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleIntermediateSession extends JdbcIntermediateSession {

  public OracleIntermediateSession(@Nullable final JdbcIntermediateFacade facade,
                                   @NotNull final DBExceptionRecognizer exceptionRecognizer,
                                   @NotNull final Connection connection,
                                   final boolean ownConnection) {
    super(facade, exceptionRecognizer, connection, ownConnection);
  }


  @Override
  protected void performPing(final Statement statement) throws SQLException {
    final String statementText = "begin null; end;";
    statement.execute(statementText);
  }

}
