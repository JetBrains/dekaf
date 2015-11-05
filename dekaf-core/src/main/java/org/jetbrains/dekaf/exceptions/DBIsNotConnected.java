package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 *
 **/
public class DBIsNotConnected extends DBException {
  public DBIsNotConnected(@NotNull final String message) {
    super(message, null);
  }
}
