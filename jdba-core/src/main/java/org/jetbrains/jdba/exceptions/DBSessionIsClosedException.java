package org.jetbrains.jdba.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBSessionIsClosedException extends DBServiceIsNotActiveException {
  public DBSessionIsClosedException(@NotNull final String message) {
    super(message);
  }
}
