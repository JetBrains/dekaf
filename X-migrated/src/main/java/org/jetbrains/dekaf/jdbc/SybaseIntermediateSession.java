package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class SybaseIntermediateSession extends JdbcIntermediateSession {

  public SybaseIntermediateSession(@Nullable final JdbcIntermediateFacade facade,
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
  protected void tuneResultSetWithFetchSize(@NotNull final ResultSet rset, final int packLimit) {
    // nothing to do
  }
}
