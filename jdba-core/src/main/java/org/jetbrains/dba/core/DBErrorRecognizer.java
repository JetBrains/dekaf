package org.jetbrains.dba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.core.errors.DBError;

import java.sql.SQLException;



/**
 * Recognizes RDBMS-specific exceptions and wrap them into unified DB-wrappers.
 */
public interface DBErrorRecognizer {

  @NotNull
  DBError recognizeError(@NotNull SQLException sqlException, @Nullable String statementText);

}
