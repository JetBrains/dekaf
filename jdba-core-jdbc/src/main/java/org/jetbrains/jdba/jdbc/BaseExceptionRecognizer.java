package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.UnknownSqlException;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;

import java.sql.SQLException;



/**
 * Base SQLException recognizer. Analyzes an {@link SQLException} instance
 * thrown by a JDBC driver and converts it to
 * a unified RDBMS- and JDBC-independent exception based on {@link DBException}.
 *
 * <p>
 *   An inheritor should override method {@link #recognizeSpecificException}.
 * </p>
 **/
public class BaseExceptionRecognizer implements DBExceptionRecognizer {

  /**
   * Analyzes the given RDBMS- JDBC-specific exception and converts it
   * to a unified indepndent {@link DBException} based one.
   * @param sqle            exception to analyze.
   * @param statementText   operation that caused to this exception (optional).
   * @return                converted exception.
   */
  @Override
  @NotNull
  public final DBException recognizeException(@NotNull final SQLException sqle,
                                              @Nullable final String statementText) {
    // unroll the exception - it's needed when the given exception is wrapped
    //                        by some external framework wrappers, connection pools, etc.
    SQLException e = sqle;
    while (e.getCause() != null && e.getCause() instanceof SQLException) {
      e = (SQLException)e.getCause();
    }

    // recognize DBMS-specific error
    DBException exception = recognizeSpecificException(e, statementText);

    // not recognized
    if (exception == null) {
      exception = new UnknownSqlException(e, statementText);
    }

    return exception;
  }


  /**
   * RDBMS-specific method to analyze exception.
   * @param sqle            exception to analyze.
   * @param statementText   operation that caused to this exception (optional).
   * @return                converted exception, or null if not recognized.
   */
  @Nullable
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    // the inheritor should override this method
    return null;
  }
}
