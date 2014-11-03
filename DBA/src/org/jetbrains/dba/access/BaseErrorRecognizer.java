package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.errors.DBError;

import java.sql.SQLException;



/**
 *
 **/
public abstract class BaseErrorRecognizer implements DBErrorRecognizer {

  @Override
  @NotNull
  public DBError recognizeError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
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
  protected abstract DBError recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText);
}
