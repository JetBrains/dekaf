package org.jetbrains.jdba.core.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBDriverException extends DBException {

  public DBDriverException(@NotNull String message, @NotNull Exception exception) {
    super(message, exception, null);
  }


  public DBDriverException(@NotNull String message) {
    super(message, null);
  }

}
