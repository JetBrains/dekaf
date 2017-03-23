package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class NoTableOrViewException extends DBException {

  public NoTableOrViewException(@NotNull final SQLException sqlException,
                                @Nullable final String statementText) {
    super(sqlException, statementText);
  }

}
