package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcSimpleSeance extends JdbcInterSeance {


  protected JdbcSimpleSeance(@NotNull final JdbcInterSession session, final String statementText) {
    super(session, statementText);

    try {
      statement = session.getConnection().prepareCall(statementText);
    }
    catch (SQLException sqle) {
      throw session.recognizeException(sqle, statementText);
    }
  }


}
