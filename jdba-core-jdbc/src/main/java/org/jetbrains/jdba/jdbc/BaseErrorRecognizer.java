package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.UnexpectedJdbcException;
import org.jetbrains.jdba.intermediate.DBErrorRecognizer;

import java.sql.SQLException;



/**
 *
 **/
public class BaseErrorRecognizer implements DBErrorRecognizer {

  @Override
  @NotNull
  public final DBException recognizeError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    // unroll the exception - it's needed when the given exception is wrapped
    //                        by some external framework wrappers, connection pools, etc.
    SQLException e = sqlException;
    while (e.getCause() != null && e.getCause() instanceof SQLException) {
      e = (SQLException)e.getCause();
    }

    // recognize DBMS-specific error
    return recognizeSpecificError(e, statementText);
  }


  @NotNull
  protected DBException recognizeSpecificError(@NotNull final SQLException sqlException,
                                               @Nullable final String statementText) {
    // the inheritor should override this method
    return new UnexpectedJdbcException(sqlException, statementText);
  }
}
