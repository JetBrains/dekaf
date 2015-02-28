package org.jetbrains.dba.core.errors;

import org.jetbrains.annotations.NotNull;



/**
 * Thrown on a problem when working with DBServiceFactory.
 * @author Leonid Bushuev from JetBrains
 */
public class DBFactoryError extends DBError {

  public DBFactoryError(@NotNull String message, @NotNull Exception exception) {
    super(message, exception, null);
  }


  public DBFactoryError(@NotNull String message) {
    super(message, null);
  }

}
