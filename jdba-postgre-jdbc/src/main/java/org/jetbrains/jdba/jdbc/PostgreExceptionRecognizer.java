package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class PostgreExceptionRecognizer extends BaseExceptionRecognizer {

  public static final PostgreExceptionRecognizer INSTANCE = new PostgreExceptionRecognizer();


  @NotNull
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    return super.recognizeSpecificException(sqle, statementText);
  }

}
