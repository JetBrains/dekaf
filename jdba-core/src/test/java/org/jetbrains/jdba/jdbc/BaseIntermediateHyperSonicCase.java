package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;

import java.sql.Connection;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseIntermediateHyperSonicCase extends BaseHyperSonicCase {

  protected static DBExceptionRecognizer ourExceptionRecognizer = new BaseExceptionRecognizer();


  protected JdbcIntermediateSession openSession() {
    Connection connection = obtainConnection();
    JdbcIntermediateSession session =
        new JdbcIntermediateSession(null, ourExceptionRecognizer, connection, true);
    return session;
  }

}
