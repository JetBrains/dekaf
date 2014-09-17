package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Abstract database access error
 */
public abstract class DBError extends RuntimeException {

  public final int vendorErrorCode;
  public final String statementText;


  protected DBError(@NotNull final SQLException sqlException, @Nullable String statementText) {
    this(sqlException.getMessage(), sqlException, sqlException.getErrorCode(), statementText);
  }


  protected DBError(@NotNull final String message, @NotNull final SQLException sqlException, @Nullable String statementText) {
    this(message, sqlException, sqlException.getErrorCode(), statementText);
  }


  protected DBError(@NotNull final String message, @NotNull final Exception exception, String statementText) {
    this(message, exception, 0, statementText);
  }


  protected DBError(@NotNull final String message, @Nullable String statementText) {
    this(message, null, 0, statementText);
  }


  private DBError(@NotNull String message, @Nullable Throwable cause, int vendorErrorCode, @Nullable String statementText) {
    super(makeErrorText(message, cause), cause);
    this.vendorErrorCode = vendorErrorCode;
    this.statementText = statementText;
  }


  private static String makeErrorText(String message, Throwable cause) {
    return cause != null && !message.endsWith(".") && !message.contains(cause.getMessage())
        ? message + " (" + cause.getMessage() + ")"
        : message;
  }


  @Override
  public String toString() {
    String message = getMessage();
    StringBuilder b = new StringBuilder(message);
    Throwable cause = getCause();
    if (cause != null && !(message.endsWith("."))) {
      String causeMessage = cause.getMessage();
      if (!message.contains(causeMessage)) b.append(' ').append('(').append(causeMessage).append(')');
    }
    if (statementText != null) {
      b.append("\nThe SQL statement:\n").append(statementText).append('\n');
    }
    return b.toString();
  }
}
