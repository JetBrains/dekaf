package org.jetbrains.jdba.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Unknown database error.
 */
public final class UnexpectedDBException extends DBException {

  public UnexpectedDBException(@NotNull final String message,
                               @NotNull final SQLException sqlException,
                               @Nullable String statementText) {
    super(sqlException, statementText);
  }

}
