package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;
import org.jetbrains.jdba.intermediate.PrimeIntermediateSession;
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
public class JdbcIntermediateFacadeTest {

  @Test
  public void basic_lifecycle_1() throws SQLException {
    // mocking

    DataSource mockDataSource = mock(DataSource.class);
    Connection mockConnection = mock(Connection.class);
    DBExceptionRecognizer mockExceptionRecognizer = mock(DBExceptionRecognizer.class);

    when(mockDataSource.getConnection()).thenReturn(mockConnection);

    // testing

    JdbcIntermediateFacade facade = new JdbcIntermediateFacade(mockDataSource, 1, mockExceptionRecognizer);

    assertThat(facade.countOpenedConnections()).isZero();
    assertThat(facade.countOpenedSessions()).isZero();

    facade.connect();

    assertThat(facade.countOpenedConnections()).isEqualTo(1);
    assertThat(facade.countOpenedSessions()).isZero();

    final PrimeIntermediateSession session = facade.openSession();

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
    DBExceptionRecognizer mockExceptionRecognizer = mock(DBExceptionRecognizer.class);

    when(mockDataSource.getConnection()).thenReturn(mockConnection);
    doNothing().doThrow(new IllegalStateException("Second attempt to close!")).when(mockConnection).close();

    // testing
    JdbcIntermediateFacade facade = new JdbcIntermediateFacade(mockDataSource, 1, mockExceptionRecognizer);
    facade.connect();

    final JdbcIntermediateSession session1 = facade.openSession();
    assertThat(session1.getConnection()).isSameAs(mockConnection);
    session1.close();

    final JdbcIntermediateSession session2 = facade.openSession();
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
    DBExceptionRecognizer mockExceptionRecognizer = mock(DBExceptionRecognizer.class);

    when(mockDataSource.getConnection()).thenReturn(mockConnection1)
                                        .thenReturn(mockConnection2)
                                        .thenThrow(new RuntimeException("Too many connections"));

    // testing
    JdbcIntermediateFacade facade = new JdbcIntermediateFacade(mockDataSource, 2, mockExceptionRecognizer);
    facade.connect();

    final JdbcIntermediateSession session1 = facade.openSession();
    final JdbcIntermediateSession session2 = facade.openSession();

    assertThat(session2.getConnection()).isNotSameAs(session1.getConnection());

    assertThat(facade.countOpenedConnections()).isEqualTo(2);
    assertThat(facade.countOpenedSessions()).isEqualTo(2);

    session1.close();
    session2.close();

    assertThat(facade.countOpenedSessions()).isZero();

    facade.disconnect();
  }


}