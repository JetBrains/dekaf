package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;



/**
 * Error codes:
 * <a href='https://technet.microsoft.com/en-us/library/cc645603(v=sql.105).aspx'>Error Codes</a>
 *
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class MssqlExceptionRecognizer extends BaseExceptionRecognizer {

  public static final MssqlExceptionRecognizer INSTANCE = new MssqlExceptionRecognizer();


  private static final Map<Integer, Class<? extends DBException>> simpleExceptionMap =
      new HashMap<Integer, Class<? extends DBException>>();

  static {
    simpleExceptionMap.put(208, NoTableOrViewException.class);
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
