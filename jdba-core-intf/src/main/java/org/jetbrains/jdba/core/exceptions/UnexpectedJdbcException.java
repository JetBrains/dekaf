package org.jetbrains.jdba.core.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class UnexpectedJdbcException extends DBException {
  public UnexpectedJdbcException(@NotNull final SQLException sqlException,
                                  @Nullable final String statementText) {
    super(sqlException, statementText);
  }
}
