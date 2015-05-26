package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.exceptions.DBException;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OracleExceptionRecognizer extends BaseExceptionRecognizer {

  public static final OracleExceptionRecognizer INSTANCE = new OracleExceptionRecognizer();


  @NotNull
  @Override
  protected DBException recognizeSpecificException(@NotNull final SQLException sqle,
                                                   @Nullable final String statementText) {
    return super.recognizeSpecificException(sqle, statementText);
  }

}
