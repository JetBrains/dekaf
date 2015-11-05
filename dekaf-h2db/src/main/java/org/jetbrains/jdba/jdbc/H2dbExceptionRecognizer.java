package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.NoRowsException;
import org.jetbrains.jdba.exceptions.NoTableOrViewException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;



/**
 * @author Leonid Bushuev from JetBrains
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class H2dbExceptionRecognizer extends BaseExceptionRecognizer {

  public static final H2dbExceptionRecognizer INSTANCE = new H2dbExceptionRecognizer();


  private static final Map<Integer, Class<? extends DBException>> simpleExceptionMap =
      new HashMap<Integer, Class<? extends DBException>>();

  static {
    simpleExceptionMap.put(42001, NoRowsException.class);
    simpleExceptionMap.put(42102, NoTableOrViewException.class);
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
