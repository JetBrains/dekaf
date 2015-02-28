package org.jetbrains.dba.rdbms.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.BaseCommandRunner;
import org.jetbrains.dba.access.BaseSession;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class OraCommandRunner extends BaseCommandRunner {

  public OraCommandRunner(@NotNull BaseSession session, @NotNull String statementSourceText, int offset) {
    super(session, statementSourceText, offset);
  }


  @Override
  public BaseCommandRunner run() {
    try {
      if (params != null) {
        PreparedStatement statement = session.getConnection().prepareStatement(statementSourceText);
        try {
          assignParameters(statement);
          statement.execute();
        }
        finally {
          statement.close();
        }
      }
      else {
        Statement statement = session.getConnection().createStatement();
        try {
          statement.setEscapeProcessing(false);
          statement.execute(statementSourceText);
        }
        finally {
          statement.close();
        }
      }
    }
    catch (SQLException e) {
      throw session.recognizeError(e, statementSourceText);
    }

    return this;
  }
}
