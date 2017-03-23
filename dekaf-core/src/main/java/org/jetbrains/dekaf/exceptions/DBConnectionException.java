package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBConnectionException extends DBException {

  public DBConnectionException(@NotNull final String message,
                               @NotNull final SQLException sqlException,
                               @Nullable final String statementText) {
    super(message, sqlException, statementText);
  }

  public DBConnectionException(@NotNull final SQLException sqlException,
                               @Nullable final String statementText) {
    super(sqlException, statementText);
  }

}
