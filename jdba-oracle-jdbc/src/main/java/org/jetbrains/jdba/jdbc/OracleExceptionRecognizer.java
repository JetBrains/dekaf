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
@SuppressWarnings("ThrowableInstanceNeverThrown")
public class OracleExceptionRecognizer extends BaseExceptionRecognizer {

  public static final OracleExceptionRecognizer INSTANCE = new OracleExceptionRecognizer();


  private static final Map<Integer, Class<? extends DBException>> simpleExceptionMap =
      new HashMap<Integer, Class<? extends DBException>>();

  static {
    simpleExceptionMap.put(942,  NoTableOrViewException.class);
    simpleExceptionMap.put(1403, NoRowsException.class);
  }



  @Nullable
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    int errCode = sqle.getErrorCode();
    final Class<? extends DBException> simpleExceptionClass = simpleExceptionMap.get(errCode);
    if (simpleExceptionClass != null) {
      return instantiateDBException(simpleExceptionClass, sqle, statementText);
    }
    else {
      return null;
    }
  }

}
