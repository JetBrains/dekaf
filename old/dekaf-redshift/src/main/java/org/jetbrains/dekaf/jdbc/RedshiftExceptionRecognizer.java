package org.jetbrains.dekaf.jdbc;



import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;

import java.sql.SQLException;



public class RedshiftExceptionRecognizer extends BaseExceptionRecognizer {

  @SuppressWarnings("WeakerAccess")
  public static final RedshiftExceptionRecognizer INSTANCE = new RedshiftExceptionRecognizer();

  @Nullable
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    String state = sqle.getSQLState();
    if ("42P01".equals(state)) {
      return instantiateDBException(NoTableOrViewException.class, sqle, statementText);
    }
    return super.recognizeSpecificException(sqle, statementText);
  }
}
