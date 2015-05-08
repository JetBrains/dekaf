package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.intermediate.DBErrorRecognizer;

import java.sql.Connection;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseIntermediateHyperSonicCase extends BaseHyperSonicCase {

  protected static DBErrorRecognizer ourErrorRecognizer = new BaseErrorRecognizer();


  protected JdbcIntermediateSession openSession() {
    Connection connection = obtainConnection();
    JdbcIntermediateSession session = new JdbcIntermediateSession(null, ourErrorRecognizer, connection, true);
    return session;
  }

}
