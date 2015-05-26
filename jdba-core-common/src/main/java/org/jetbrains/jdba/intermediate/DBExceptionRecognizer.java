package org.jetbrains.jdba.intermediate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;

import java.sql.SQLException;



/**
 * Recognizes RDBMS-specific exceptions and wrap them into unified DB-wrappers.
 */
public interface DBExceptionRecognizer {

  @NotNull
  DBException recognizeException(@NotNull SQLException sqlException, @Nullable String statementText);

}
