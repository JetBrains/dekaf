package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.errors.DBError;
import org.jetbrains.dba.errors.DuplicateKeyError;
import org.jetbrains.dba.errors.UnknownDBError;

import java.sql.SQLException;
import java.util.regex.Pattern;



/**
 * Oracle specific errors recognizer.
 **/
public class OraErrorRecognizer extends BaseErrorRecognizer {

  @NotNull
  @Override
  protected DBError recognizeSpecificError(@NotNull final SQLException sqlException) {
    int code = sqlException.getErrorCode();

    switch (code) {
      case 1:
        return new DuplicateKeyError(sqlException);
      default:
        String msg = sqlException.getMessage().trim();
        boolean hasNumber = NUMBERED_ERROR_PATTERN.matcher(msg).matches();
        if (!hasNumber) msg = "Oracle SQL error " + code + ": " + msg;
        return new UnknownDBError(msg, sqlException);
    }
  }

  private static final Pattern NUMBERED_ERROR_PATTERN =
    Pattern.compile("^\\w{3}-\\d+:.*$");

}
