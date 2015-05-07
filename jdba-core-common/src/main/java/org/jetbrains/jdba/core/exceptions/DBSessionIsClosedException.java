package org.jetbrains.jdba.core.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBSessionIsClosedException extends DBException {
  public DBSessionIsClosedException(@NotNull final String message) {
    super(message, null);
  }
}
