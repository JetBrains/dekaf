package org.jetbrains.jdba.core.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 *
 **/
public class DuplicateKeyError extends DBError {

  public DuplicateKeyError(@NotNull final SQLException sqlException, @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public DuplicateKeyError(@NotNull final String message, @NotNull final SQLException sqlException, @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public DuplicateKeyError(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}
