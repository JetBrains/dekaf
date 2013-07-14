package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;



/**
 * Unknown database error.
 */
public final class UnknownDBError extends DBError {

  public UnknownDBError(@NotNull final SQLException sqlException) {
    super(sqlException);
  }


  public UnknownDBError(@NotNull final String message, @NotNull final SQLException sqlException) {
    super(message, sqlException);
  }


  public UnknownDBError(@NotNull final String message) {
    super(message);
  }
}
