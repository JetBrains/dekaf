package org.jetbrains.jdba.core.exceptions;

import org.jetbrains.annotations.NotNull;



/**
 * Thrown on a problem when working with DBServiceFactory.
 * @author Leonid Bushuev from JetBrains
 */
public class DBFactoryException extends DBException {

  public DBFactoryException(@NotNull String message, @NotNull Exception exception) {
    super(message, exception, null);
  }


  public DBFactoryException(@NotNull String message) {
    super(message, null);
  }

}
