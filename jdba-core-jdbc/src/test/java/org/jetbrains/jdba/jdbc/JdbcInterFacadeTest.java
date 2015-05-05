package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.core.DBErrorRecognizer;
import org.jetbrains.jdba.core.DBInterSession;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
public class JdbcInterFacadeTest {

  @Test
  public void basic_lifecycle_1() throws SQLException {
    // mocking

    DataSource mockDataSource = mock(DataSource.class);
    Connection mockConnection = mock(Connection.class);
    DBErrorRecognizer mockErrorRecognizer = mock(DBErrorRecognizer.class);

    when(mockDataSource.getConnection()).thenReturn(mockConnection);

    // testing

    JdbcInterFacade facade = new JdbcInterFacade(mockDataSource, 1, mockErrorRecognizer);

    assertThat(facade.countOpenedConnections()).isZero();
    assertThat(facade.countOpenedSessions()).isZero();

    facade.connect();

    assertThat(facade.countOpenedConnections()).isEqualTo(1);
    assertThat(facade.countOpenedSessions()).isZero();

    final DBInterSession session = facade.openSession();

    assertThat(facade.countOpenedConnections()).isEqualTo(1);
    assertThat(facade.countOpenedSessions()).isEqualTo(1);

    session.close();

    assertThat(facade.countOpenedConnections()).isEqualTo(1);
    assertThat(facade.countOpenedSessions()).isZero();

    facade.disconnect();

    assertThat(facade.countOpenedConnections()).isZero();
    assertThat(facade.countOpenedSessions()).isZero();

    // post-verifying
    verify(mockConnection, atLeastOnce()).close();
  }

  @Test
  public void basic_lifecycle_2() throws SQLException {
    // mocking
    DataSource mockDataSource = mock(DataSource.class);
    Connection mockConnection = mock(Connection.class);
    DBErrorRecognizer mockErrorRecognizer = mock(DBErrorRecognizer.class);

    when(mockDataSource.getConnection()).thenReturn(mockConnection);
    doNothing().doThrow(new IllegalStateException("Second attempt to close!")).when(mockConnection).close();

    // testing
    JdbcInterFacade facade = new JdbcInterFacade(mockDataSource, 1, mockErrorRecognizer);
    facade.connect();

    final JdbcInterSession session1 = facade.openSession();
    assertThat(session1.getConnection()).isSameAs(mockConnection);
    session1.close();

    final JdbcInterSession session2 = facade.openSession();
    assertThat(session2.getConnection()).isSameAs(mockConnection);
    session2.close();

    assertThat(session2).isNotSameAs(session1);

    facade.disconnect();

    // post-verifying
    verify(mockConnection, atLeastOnce()).close();
  }

  @Test
  public void basic_lifecycle_2_parallel() throws SQLException {
    // mocking
    DataSource mockDataSource = mock(DataSource.class);
    Connection mockConnection1 = mock(Connection.class);
    Connection mockConnection2 = mock(Connection.class);
    DBErrorRecognizer mockErrorRecognizer = mock(DBErrorRecognizer.class);

    when(mockDataSource.getConnection()).thenReturn(mockConnection1)
                                        .thenReturn(mockConnection2)
                                        .thenThrow(new RuntimeException("Too many connections"));

    // testing
    JdbcInterFacade facade = new JdbcInterFacade(mockDataSource, 2, mockErrorRecognizer);
    facade.connect();

    final JdbcInterSession session1 = facade.openSession();
    final JdbcInterSession session2 = facade.openSession();

    assertThat(session2.getConnection()).isNotSameAs(session1.getConnection());

    assertThat(facade.countOpenedConnections()).isEqualTo(2);
    assertThat(facade.countOpenedSessions()).isEqualTo(2);

    session1.close();
    session2.close();

    assertThat(facade.countOpenedSessions()).isZero();

    facade.disconnect();
  }


}