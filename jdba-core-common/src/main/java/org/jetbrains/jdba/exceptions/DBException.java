package org.jetbrains.jdba.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Abstract database access error
 */
public abstract class DBException extends RuntimeException {

  public final int vendorErrorCode;
  public final String statementText;


  protected DBException(@NotNull final SQLException sqlException, @Nullable String statementText) {
    this(sqlException.getMessage(), sqlException, sqlException.getErrorCode(), statementText);
  }


  protected DBException(@NotNull final String message,
                        @NotNull final SQLException sqlException,
                        @Nullable String statementText) {
    this(message, sqlException, sqlException.getErrorCode(), statementText);
  }


  protected DBException(@NotNull final String message,
                        @NotNull final Exception exception,
                        String statementText) {
    this(message, exception, 0, statementText);
  }


  protected DBException(@NotNull final String message, @Nullable String statementText) {
    this(message, null, 0, statementText);
  }


  public DBException(String message, Throwable cause, @Nullable String statementText) {
    this(message, cause, 0, statementText);
  }


  private DBException(@NotNull String message,
                      @Nullable Throwable cause,
                      int vendorErrorCode,
                      @Nullable String statementText) {
    super(makeErrorText(message, cause), cause);
    this.vendorErrorCode = vendorErrorCode;
    this.statementText = statementText;
  }


  private static String makeErrorText(@NotNull String message, @Nullable Throwable cause) {
    String causeMessage = cause != null
        ? cause.getMessage()
        : null;
    return causeMessage != null && !message.endsWith(".") && !message.contains(causeMessage)
        ? message + " (" + causeMessage + ")"
        : message;
  }


  @Override
  public String toString() {
    String message = getMessage();
    StringBuilder b = new StringBuilder(message);
    if (statementText != null) {
      b.append("\nThe SQL statement:\n").append(statementText).append('\n');
    }
    return b.toString();
  }
}
