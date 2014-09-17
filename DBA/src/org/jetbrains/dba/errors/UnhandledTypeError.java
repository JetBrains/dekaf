package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 *
 **/
public class UnhandledTypeError extends DBError {
  public UnhandledTypeError(@NotNull final String message, @Nullable String statementText) {
    super(message, statementText);
  }
}
