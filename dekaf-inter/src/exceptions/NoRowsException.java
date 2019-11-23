package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 *
 **/
public class NoRowsException extends DBException {

  public NoRowsException(@NotNull final SQLException sqlException, @Nullable String statementText) {
    super(sqlException, statementText);
  }


  public NoRowsException(@NotNull final String message,
                         @NotNull final SQLException sqlException,
                         @Nullable String statementText) {
    super(message, sqlException, statementText);
  }


  public NoRowsException(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}
