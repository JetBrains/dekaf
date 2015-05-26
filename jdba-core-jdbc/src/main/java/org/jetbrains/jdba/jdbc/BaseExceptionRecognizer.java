package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.UnknownDBException;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;

import java.lang.reflect.Constructor;
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
   * to a unified independent {@link DBException} based one.
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
      exception = new UnknownDBException(e, statementText);
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


  @Nullable
  protected <E extends DBException> E instantiateDBException(
                                             final @NotNull Class<? extends E> exceptionClass,
                                             final @NotNull SQLException sqle,
                                             final @Nullable String statementText) {
    try {
      final Constructor<? extends E> constructor =
          exceptionClass.getConstructor(SQLException.class, String.class);
      return constructor.newInstance(sqle, statementText);
    }
    catch (Exception e) {
      // TODO log it somehow
      return null;
    }
  }

}
