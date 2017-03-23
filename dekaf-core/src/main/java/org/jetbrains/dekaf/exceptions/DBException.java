package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Abstract database access error
 */
public abstract class DBException extends RuntimeException {

  public final int vendorErrorCode;
  public final String statementText;


  protected DBException(@NotNull final SQLException sqlException,
                        @Nullable final String statementText) {
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


  protected DBException(@NotNull final String message,
                        @Nullable final String statementText) {
    this(message, null, 0, statementText);
  }


  public DBException(@NotNull final String message,
                     @Nullable final Throwable cause,
                     @Nullable final String statementText) {
    this(message, cause, 0, statementText);
  }


  private DBException(@NotNull final String message,
                      @Nullable final Throwable cause,
                      final int vendorErrorCode,
                      @Nullable final String statementText) {
    super(makeErrorText(message, cause), stripException(cause));
    this.vendorErrorCode = vendorErrorCode;
    this.statementText = statementText;
  }


  @NotNull
  private static String makeErrorText(@NotNull final String message,
                                      @Nullable final Throwable cause) {
    String causeMessage = cause != null
        ? cause.getMessage()
        : null;
    return causeMessage != null && !message.endsWith(".") && !message.contains(causeMessage)
        ? message + " (" + causeMessage + ")"
        : message;
  }


  @Contract("null->null; !null->!null")
  @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ThrowableInstanceNeverThrown"})
  private static Throwable stripException(final Throwable e) {
    if (e == null) return null;

    String className = e.getClass().getName();
    boolean classOk = e instanceof DBException
                   || className.startsWith("java.")
                   || className.startsWith("javax.")
                   || className.startsWith("sun.jdbc.odbc.");

    Throwable originalCause = e.getCause();
    Throwable strippedCause = stripException(originalCause);
    boolean causeOk = originalCause == strippedCause;

    if (classOk && causeOk) return e;

    Throwable strippedException;
    if (e instanceof SQLException) {
      strippedException = new StrippedSQLException((SQLException) e, strippedCause);
    }
    else {
      strippedException = new StrippedUnknownException(e, strippedCause);
    }

    return strippedException;
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
