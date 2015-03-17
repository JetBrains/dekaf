package org.jetbrains.jdba.core.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 *
 **/
public class NoRowsError extends DBError {

  public NoRowsError(@NotNull final SQLException sqlException, @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public NoRowsError(@NotNull final String message, @NotNull final SQLException sqlException, @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public NoRowsError(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}
