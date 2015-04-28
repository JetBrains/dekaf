package org.jetbrains.jdba.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.exceptions.DBException;
import org.jetbrains.jdba.core.exceptions.DuplicateKeyException;
import org.jetbrains.jdba.core.exceptions.UnknownDBException;
import org.jetbrains.jdba.jdbc.BaseErrorRecognizer;

import java.sql.SQLException;



/**
 * MySQL specific errors recognizer.
 **/
public class MysqlErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBException recognizeSpecificError(@NotNull final SQLException sqlException, @Nullable final String statementText) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 1062:
        return new DuplicateKeyException(sqlException, statementText);
      default:
        return new UnknownDBException(sqlException, statementText);
    }
  }
}
