package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.ParameterDef;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcCallableStatementSeance extends JdbcInterSeance {


  @NotNull
  protected final ParameterDef[] myOutParameterDefs;


    protected JdbcCallableStatementSeance(@NotNull final JdbcInterSession session,
                                          @NotNull final String statementText,
                                          @NotNull final ParameterDef[] outParameterDefs) {
    super(session, statementText);
    this.myOutParameterDefs = outParameterDefs;

    try {
      CallableStatement stmt = session.getConnection().prepareCall(statementText);
      for (int i = 0, n = outParameterDefs.length; i < n; i++) {
        ParameterDef d = outParameterDefs[i];
        if (d == null) continue;

        // TODO register it correctly
        stmt.registerOutParameter(i+1, Types.VARCHAR);
      }
      statement = stmt;
    }
    catch (SQLException sqle) {
      throw session.recognizeException(sqle, statementText);
    }
  }


  @Override
  public void close() {
    // TODO close all non-default cursors here

    super.close();
  }
}
