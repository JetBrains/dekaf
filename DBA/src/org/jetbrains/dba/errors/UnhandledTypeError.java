package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;



/**
 *
 **/
public class UnhandledTypeError extends DBError {
  public UnhandledTypeError(@NotNull final String message) {
    super(message);
  }
}
