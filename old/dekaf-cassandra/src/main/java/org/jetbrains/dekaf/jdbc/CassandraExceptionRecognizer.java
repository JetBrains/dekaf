package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;
import org.jetbrains.dekaf.exceptions.UnknownDBException;

import java.sql.SQLException;



public class CassandraExceptionRecognizer extends BaseExceptionRecognizer {

  public static final CassandraExceptionRecognizer INSTANCE = new CassandraExceptionRecognizer();

  @Nullable
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    Throwable cause = sqle.getCause();
    if (cause == null) return new UnknownDBException(sqle, statementText);
    if (cause.getMessage().startsWith("unconfigured table") ||
        cause.getMessage().startsWith("unconfigured view")) {
      return new NoTableOrViewException(sqle, statementText);
    }
    return new UnknownDBException(sqle, statementText);
  }
}
