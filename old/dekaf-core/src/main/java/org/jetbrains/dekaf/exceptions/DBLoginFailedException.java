package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class DBLoginFailedException extends DBConnectionException {

  public DBLoginFailedException(@NotNull final SQLException sqlException,
                                @Nullable final String statementText) {
    super(sqlException, statementText);
  }

}
