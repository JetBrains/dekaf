package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBErrorRecognizer;
import org.jetbrains.jdba.core.exceptions.DBException;
import org.jetbrains.jdba.core.exceptions.UnexpectedJdbcException;

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
