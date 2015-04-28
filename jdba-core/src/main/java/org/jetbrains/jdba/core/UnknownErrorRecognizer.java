package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.exceptions.DBException;
import org.jetbrains.jdba.core.exceptions.UnknownDBException;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class UnknownErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBException recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    return new UnknownDBException(sqlException, statementText);
  }

}
