package org.jetbrains.dba.core.errors;

import org.jetbrains.annotations.NotNull;



/**
 *
 **/
public class DBIsNotConnected extends DBError {
  public DBIsNotConnected(@NotNull final String message) {
    super(message, null);
  }
}
