package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 *
 **/
public class DuplicateKeyException extends DBException {

  public DuplicateKeyException(@NotNull final SQLException sqlException,
                               @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public DuplicateKeyException(@NotNull final String message,
                               @NotNull final SQLException sqlException,
                               @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public DuplicateKeyException(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}
