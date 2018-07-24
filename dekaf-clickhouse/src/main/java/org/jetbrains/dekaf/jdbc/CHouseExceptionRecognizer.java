package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class CHouseExceptionRecognizer extends BaseExceptionRecognizer {

  public static final CHouseExceptionRecognizer INSTANCE = new CHouseExceptionRecognizer();

  private static final Map<String, Class<? extends DBException>> simpleExceptionMap =
      new HashMap<String, Class<? extends DBException>>();

  static {
    simpleExceptionMap.put("42000", NoTableOrViewException.class);
  }



  @Nullable
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    String errCode = sqle.getSQLState();
    Class<? extends DBException> simpleExceptionClass = simpleExceptionMap.get(errCode);
    if (simpleExceptionClass != null) {
      return instantiateDBException(simpleExceptionClass, sqle, statementText);
    }
    else {
      return null;
    }
  }
}
