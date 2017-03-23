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
    return getConnection().prepareStatement(statementText,
                                            ResultSet.TYPE_FORWARD_ONLY,
                                            ResultSet.CONCUR_READ_ONLY);
  }

  @Override
  protected void tuneStatementWithFetchSize(final PreparedStatement stmt,
                                            final int packLimit) throws SQLException {
    final boolean rowByRow;
    switch (getFetchStrategy()) {
      case MysqlConsts.FETCH_STRATEGY_ROW:
        rowByRow = true;
        break;
      case MysqlConsts.FETCH_STRATEGY_AUTO:
        rowByRow = packLimit > 0;
        break;
      case MysqlConsts.FETCH_STRATEGY_WHOLE:
      default:
        rowByRow = false;
        break;
    }

    if (rowByRow) {
      stmt.setFetchSize(Integer.MIN_VALUE);
    }
  }


  @Override
  protected void tuneResultSetWithFetchSize(@NotNull final ResultSet rset, final int packLimit) {
    // nothing to do
  }

  public byte getFetchStrategy() {
    MysqlIntermediateFacade facade = myFacade instanceof MysqlIntermediateFacade
        ? (MysqlIntermediateFacade) myFacade
        : null;
    return facade != null ? facade.getFetchStrategy() : MysqlConsts.FETCH_STRATEGY_AUTO;
  }

}
