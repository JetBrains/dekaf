package org.jetbrains.dba.core.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Unknown database error.
 */
public final class UnknownDBError extends DBError {

  public UnknownDBError(@NotNull final SQLException sqlException, @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public UnknownDBError(@NotNull final String message, @NotNull final SQLException sqlException, @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public UnknownDBError(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}
