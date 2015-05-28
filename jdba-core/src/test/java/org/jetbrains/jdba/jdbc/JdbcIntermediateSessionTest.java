package org.jetbrains.jdba.jdbc;

import org.junit.Test;

import java.sql.Connection;



public class JdbcIntermediateSessionTest extends BaseIntermediateHyperSonicCase {


  @Test
  public void create_close() {
    Connection connection = obtainConnection();
    JdbcIntermediateSession session = new JdbcIntermediateSession(null,
                                                                  ourExceptionRecognizer, connection, true);
    session.close();
  }


}