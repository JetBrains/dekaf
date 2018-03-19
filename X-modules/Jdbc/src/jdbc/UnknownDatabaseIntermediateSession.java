package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * @author Leonid Bushuev
 **/
public class UnknownDatabaseIntermediateSession extends JdbcIntermediateSession {

  @Nullable
  private UnknownDatabaseInfo myUnknownInfo = null;

  public UnknownDatabaseIntermediateSession(@Nullable final JdbcIntermediateFacade facade,
                                            @NotNull final DBExceptionRecognizer exceptionRecognizer,
                                            @NotNull final Connection connection,
                                            final boolean ownConnection) {
    super(facade, exceptionRecognizer, connection, ownConnection);
  }


  @NotNull
  private UnknownDatabaseInfo getUnknownInfo() {
    if (myUnknownInfo == null) {
      if (myFacade != null) {
        myUnknownInfo = ((UnknownDatabaseIntermediateFacade)myFacade).getUnknownInfo();
      }
      if (myUnknownInfo == null) {
        try {
          myUnknownInfo = UnknownDatabaseInfoHelper.obtainDatabaseInfo(getConnection());
        }
        catch (SQLException e) {
          throw recognizeException(e);
        }
      }
    }
    return myUnknownInfo;
  }


  @Override
  public int ping() {
    getUnknownInfo(); // to pre-load it
    return super.ping();
  }

  @Override
  protected void performPing(final Statement statement) throws SQLException {
    UnknownDatabaseInfo ui = getUnknownInfo();
    String pingQuery = "select 1" + ui.fromSingleRowTable;
    statement.execute(pingQuery);
  }

}
