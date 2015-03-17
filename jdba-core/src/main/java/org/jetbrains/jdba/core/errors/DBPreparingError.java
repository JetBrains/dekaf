package org.jetbrains.jdba.core.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Throws when a problem of a SQL command or query initialization error occured.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class DBPreparingError extends DBError {
  public DBPreparingError(@NotNull final SQLException sqlException, @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public DBPreparingError(@NotNull final String message, @NotNull final SQLException sqlException, @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public DBPreparingError(@NotNull final String message, @NotNull final Exception exception) {
    super(message, exception, null);
  }


  public DBPreparingError(@NotNull final String message) {
    super(message, null);
  }
}
