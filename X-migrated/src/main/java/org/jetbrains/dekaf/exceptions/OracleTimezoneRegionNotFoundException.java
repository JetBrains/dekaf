package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleTimezoneRegionNotFoundException extends DBConnectionException {

  public OracleTimezoneRegionNotFoundException(@NotNull final String message,
                                               @NotNull final SQLException sqle,
                                               @Nullable final String statementText) {
    super(message, sqle, statementText);
  }

  public OracleTimezoneRegionNotFoundException(@NotNull final SQLException sqlException,
                                               @Nullable final String statementText) {
    super(sqlException, statementText);
  }

}
