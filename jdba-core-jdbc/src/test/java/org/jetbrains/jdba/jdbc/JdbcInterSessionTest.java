package org.jetbrains.jdba.jdbc;

import org.junit.Test;

import java.sql.Connection;



public class JdbcInterSessionTest extends BaseHyperSonicCase {


  @Test
  public void create_close() {
    Connection connection = obtainConnection();
    JdbcInterSession session = new JdbcInterSession(new BaseErrorRecognizer(), connection, true);
    session.close();
  }


}