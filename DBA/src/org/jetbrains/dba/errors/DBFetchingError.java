package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;



/**
 * Throws when a problem of a SQL command or query initialization error occured.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class DBFetchingError extends DBError {
  public DBFetchingError(@NotNull final SQLException sqlException) {
    super(sqlException);
  }


  public DBFetchingError(@NotNull final String message, @NotNull final SQLException sqlException) {
    super(message, sqlException);
  }


  public DBFetchingError(@NotNull final String message, @NotNull final Exception exception) {
    super(message, exception);
  }


  public DBFetchingError(@NotNull final String message) {
    super(message);
  }
}
