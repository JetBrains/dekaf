package org.jetbrains.dba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.core.errors.DBError;
import org.jetbrains.dba.core.errors.UnknownDBError;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class UnknownErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBError recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    return new UnknownDBError(sqlException, statementText);
  }

}
