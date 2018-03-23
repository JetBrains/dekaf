package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;

import java.sql.Connection;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseIntermediateInMemoryDBCase extends BaseInMemoryDBCase {

  protected static DBExceptionRecognizer ourExceptionRecognizer = new BaseExceptionRecognizer();


  protected JdbcIntermediateSession openSession() {
    Connection connection = obtainConnection();
    JdbcIntermediateSession session =
        new JdbcIntermediateSession(null, ourExceptionRecognizer, connection, true);
    return session;
  }

}
