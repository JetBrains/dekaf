package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class DBSchemaAccessDeniedException extends DBAccessDeniedException {

  public DBSchemaAccessDeniedException(@NotNull final SQLException sqlException,
                                       @Nullable final String statementText) {
    super(sqlException, statementText);
  }

}
