package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.core.DBLeasedSession;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcSessionsTest extends BaseInMemoryDBCase {


  private UnknownDatabaseProvider provider = new UnknownDatabaseProvider();


  @Test
  public void wrap_connection_not_own() throws SQLException {

    final Connection connection = obtainConnection();
    assertThat(connection.isClosed()).isFalse();

    final DBLeasedSession session = JdbcSessions.wrap(connection, provider, false);

    assertThat(session.isClosed()).isFalse();

    session.close();

    assertThat(session.isClosed()).isTrue();

    assertThat(connection.isClosed()).isFalse();

    connection.close();

    assertThat(connection.isClosed()).isTrue();
  }

  @Test
  public void wrap_connection_ownership() throws SQLException {

    final Connection connection = obtainConnection();
    assertThat(connection.isClosed()).isFalse();

    final DBLeasedSession session = JdbcSessions.wrap(connection, provider, true);

    assertThat(session.isClosed()).isFalse();

    session.close();

    assertThat(session.isClosed()).isTrue();

    assertThat(connection.isClosed()).isTrue();
  }

}