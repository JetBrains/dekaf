package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.errors.DBError;

import java.sql.SQLException;



/**
 *
 **/
public abstract class DatabaseAbstractErrorRecognizer implements DBErrorRecognizer {

  @NotNull
  public DBError recognizeError(@NotNull final SQLException sqlException) {
    // unroll the exception - it's needed when the given exception is wrapped
    //                        by some external framework wrappers, connection pools, etc.
    SQLException e = sqlException;
    while (e.getCause() != null && e.getCause() instanceof SQLException) {
      e = (SQLException)e.getCause();
    }

    // recognize DBMS-specific error
    return recognizeSpecificError(e);
  }


  @NotNull
  protected abstract DBError recognizeSpecificError(@NotNull final SQLException sqlException);
}
