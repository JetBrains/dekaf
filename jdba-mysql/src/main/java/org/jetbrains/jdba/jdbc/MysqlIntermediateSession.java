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
public class MysqlIntermediateSession extends JdbcIntermediateSession {

  public MysqlIntermediateSession(@Nullable final JdbcIntermediateFacade facade,
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
    PreparedStatement stmt = getConnection().prepareStatement(statementText,
                                                              ResultSet.TYPE_FORWARD_ONLY,
                                                              ResultSet.CONCUR_READ_ONLY);
    // stmt.setFetchSize(Integer.MIN_VALUE); // it looks like this slows down fetching operations
    return stmt;
  }

  @Override
  void tuneResultSet(@NotNull final ResultSet rset) throws SQLException {
    // nothing to do
  }
}
