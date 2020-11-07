package org.jetbrains.dekaf.inter.exceptions;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;



/**
 * Throws when a problem of a SQL command or query is performed.
 *
 * @author Leonid Bushuev
 */
public class DBPerformingException extends DBException {

  public DBPerformingException(@NotNull final SQLException sqlException,
                               @NotNull final String statementText) {
    super(sqlException, statementText);
  }

}
