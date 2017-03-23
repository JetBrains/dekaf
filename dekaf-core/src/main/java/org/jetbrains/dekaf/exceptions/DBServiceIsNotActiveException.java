package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBServiceIsNotActiveException extends DBException {

  public DBServiceIsNotActiveException(@NotNull final String message) {
    super(message, null);
  }

}
