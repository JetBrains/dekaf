package org.jetbrains.dba.errors;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBDriverError extends DBError {

  public DBDriverError(@NotNull String message, @NotNull Exception exception) {
    super(message, exception);
  }


  public DBDriverError(@NotNull String message) {
    super(message);
  }

}
