package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * Throws when a problem of a SQL command or query initialization error occured.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class DBPreparingException extends DBException {
  public DBPreparingException(@NotNull final SQLException sqlException,
                              @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public DBPreparingException(@NotNull final String message,
                              @NotNull final SQLException sqlException,
                              @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public DBPreparingException(@NotNull final String message, @NotNull final Exception exception) {
    super(message, exception, null);
  }


  public DBPreparingException(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }

}
