package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class DBAccessDeniedException extends DBException {

  public DBAccessDeniedException(@NotNull final SQLException sqlException,
                                 @Nullable final String statementText) {
    super(sqlException, statementText);
  }

}
