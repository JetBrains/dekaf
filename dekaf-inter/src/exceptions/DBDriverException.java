package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBDriverException extends DBException {

  public DBDriverException(@NotNull String message, @NotNull Exception exception) {
    super(message, exception, null);
  }

  public DBDriverException(@NotNull String message, @NotNull SQLException exception) {
    super(message, exception, null);
  }


  public DBDriverException(@NotNull String message) {
    super(message, null);
  }

}
