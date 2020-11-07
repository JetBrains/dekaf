package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 *
 **/
public class DBIsNotConnected extends DBException {
  public DBIsNotConnected(@NotNull final String message) {
    super(message, null);
  }
}
