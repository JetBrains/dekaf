package org.jetbrains.dekaf.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class DBColumnAccessDeniedException extends DBAccessDeniedException {

  public DBColumnAccessDeniedException(@NotNull final SQLException sqlException,
                                       @Nullable final String statementText) {
    super(sqlException, statementText);
  }

}
