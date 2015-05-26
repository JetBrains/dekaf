package org.jetbrains.jdba.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class UnknownSqlException extends DBException {
  public UnknownSqlException(@NotNull final SQLException sqlException,
                             @Nullable final String statementText) {
    super(sqlException, statementText);
  }
}
