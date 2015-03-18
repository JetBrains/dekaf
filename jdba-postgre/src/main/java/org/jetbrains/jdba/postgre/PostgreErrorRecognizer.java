package org.jetbrains.jdba.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.BaseErrorRecognizer;
import org.jetbrains.jdba.core.errors.DBError;
import org.jetbrains.jdba.core.errors.DuplicateKeyError;
import org.jetbrains.jdba.core.errors.UnknownDBError;

import java.sql.SQLException;



/**
 * PostgreSQL specific errors recognizer.
 **/
public class PostgreErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBError recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 1:
        return new DuplicateKeyError(sqlException, statementText);
      default:
        return new UnknownDBError(sqlException, statementText);
    }
  }
}
