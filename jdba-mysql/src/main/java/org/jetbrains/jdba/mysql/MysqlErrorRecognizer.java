package org.jetbrains.jdba.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.BaseErrorRecognizer;
import org.jetbrains.jdba.core.errors.DBError;
import org.jetbrains.jdba.core.errors.DuplicateKeyError;
import org.jetbrains.jdba.core.errors.UnknownDBError;

import java.sql.SQLException;



/**
 * MySQL specific errors recognizer.
 **/
public class MysqlErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBError recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 1062:
        return new DuplicateKeyError(sqlException, statementText);
      default:
        return new UnknownDBError(sqlException, statementText);
    }
  }
}
