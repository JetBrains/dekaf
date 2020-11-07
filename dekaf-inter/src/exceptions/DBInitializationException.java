package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;



/**
 * Throws when a problem of a SQL command or query initialization error occured.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class DBInitializationException extends DBException {

  public DBInitializationException(@NotNull final String message,
                                   @NotNull final SQLException sqlException) {
    super(message, sqlException, null);
  }


  public DBInitializationException(@NotNull final String message,
                                   @NotNull final Exception exception) {
    super(message, exception, null);
  }


  public DBInitializationException(@NotNull final String message) {
    super(message, (Throwable)null, null);
  }

}
