package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.exceptions.NoRowsException;
import org.jetbrains.dekaf.exceptions.NoTableOrViewException;
import org.jetbrains.dekaf.exceptions.OracleTimezoneRegionNotFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



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
    simpleExceptionMap.put(1882, OracleTimezoneRegionNotFoundException.class);
  }



  @Nullable
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    int errCode = sqle.getErrorCode();
    String errMsg = sqle.getMessage();

    if (errCode == 604 && errMsg != null) errCode = parseRealErrCode(errMsg);

    final Class<? extends DBException> simpleExceptionClass = simpleExceptionMap.get(errCode);
    if (simpleExceptionClass != null) {
      return instantiateDBException(simpleExceptionClass, sqle, statementText);
    }
    else {
      return null;
    }
  }


  private static final Pattern ORA_ERROR_PATTERN = Pattern.compile("ORA-(\\d{5}):");

  private int parseRealErrCode(@NotNull final String errMsg) {
    Matcher m = ORA_ERROR_PATTERN.matcher(errMsg);
    boolean found = m.find();
    while (found) {
      int code = Integer.parseInt(m.group(1), 10);
      if (code > 0 && code != 604) return code;
      found = m.find(m.end());
    }

    return 604;
  }

}
