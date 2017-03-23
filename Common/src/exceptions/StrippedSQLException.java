package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class StrippedSQLException extends SQLException {

  @NotNull
  public final String originalClassName;

  public StrippedSQLException(@NotNull final SQLException originalException,
                              @Nullable final Throwable cause) {
    super(prepareMessage(originalException),
          originalException.getSQLState(),
          originalException.getErrorCode(),
          cause);
    originalClassName = originalException.getClass().getName();
    setStackTrace(originalException.getStackTrace());
  }

  private static String prepareMessage(@NotNull final SQLException originalException) {
    return originalException.getClass().getName() + ": " + originalException.getMessage();
  }

}
