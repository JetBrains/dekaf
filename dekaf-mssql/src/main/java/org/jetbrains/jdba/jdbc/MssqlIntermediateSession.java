package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class MssqlIntermediateSession extends JdbcIntermediateSession {

  public MssqlIntermediateSession(@Nullable final JdbcIntermediateFacade facade,
                                  @NotNull final DBExceptionRecognizer exceptionRecognizer,
                                  @NotNull final Connection connection,
                                  final boolean ownConnection) {
    super(facade, exceptionRecognizer, connection, ownConnection);
  }


  @NotNull
  @Override
  PreparedStatement prepareSimpleStatement(@NotNull final String statementText)
      throws SQLException
  {
    //noinspection UnnecessaryLocalVariable
    PreparedStatement stmt = getConnection().prepareStatement(statementText,
                                                              ResultSet.TYPE_FORWARD_ONLY,
                                                              ResultSet.CONCUR_READ_ONLY);
    return stmt;
  }


  @Override
  void tuneResultSetWithFetchSize(@NotNull final ResultSet rset, final int fetchLimit) {
    // nothing to do
  }
}
