package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;



/**
 * Error codes:
 * // TODO find the correct list of error codes
 *
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class SybaseExceptionRecognizer extends BaseExceptionRecognizer {

  public static final SybaseExceptionRecognizer INSTANCE = new SybaseExceptionRecognizer();


  private static final Map<Integer, Class<? extends DBException>> simpleExceptionMap =
      new HashMap<Integer, Class<? extends DBException>>();

  static {
    simpleExceptionMap.put(208,   NoTableOrViewException.class);
    simpleExceptionMap.put(4002,  DBLoginFailedException.class);
    simpleExceptionMap.put(10332, DBColumnAccessDeniedException.class);
    simpleExceptionMap.put(10351, DBSchemaAccessDeniedException.class);
  }



  @Nullable
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    int errCode = sqle.getErrorCode();
    Class<? extends DBException> simpleExceptionClass = simpleExceptionMap.get(errCode);
    if (simpleExceptionClass != null) {
      return instantiateDBException(simpleExceptionClass, sqle, statementText);
    }
    else {
      return null;
    }
  }


}
