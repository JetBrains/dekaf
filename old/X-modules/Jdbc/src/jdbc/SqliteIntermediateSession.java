package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import java.sql.Connection;

public class SqliteIntermediateSession extends JdbcIntermediateSession {

  SqliteIntermediateSession(@Nullable JdbcIntermediateFacade facade,
                            @NotNull DBExceptionRecognizer exceptionRecognizer,
                            @NotNull Connection connection, boolean ownConnection) {
    super(facade, exceptionRecognizer, connection, ownConnection);
  }

  @NotNull
  @Override
  protected JdbcIntermediateSimpleSeance openSimpleStatementSeance(@NotNull String statementText) {
    return new SqliteIntermediateSimpleSeance(this, statementText);
  }
}
