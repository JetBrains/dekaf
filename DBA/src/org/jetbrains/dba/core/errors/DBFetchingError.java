package org.jetbrains.dba.core.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Throws when a problem of a SQL command or query initialization error occured.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class DBFetchingError extends DBError {
  public DBFetchingError(@NotNull final SQLException sqlException, @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public DBFetchingError(@NotNull final String message, @NotNull final SQLException sqlException, @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public DBFetchingError(@NotNull final String message, @NotNull final Exception exception, @Nullable String statementText) {
    super(message, exception, statementText);
  }


  public DBFetchingError(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}
