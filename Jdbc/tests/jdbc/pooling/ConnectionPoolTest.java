package org.jetbrains.dekaf.jdbc.pooling;

import org.jetbrains.dekaf.junitft.ThreadTestUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static org.assertj.core.api.BDDAssertions.then;



@FixMethodOrder(MethodSorters.JVM)
public class ConnectionPoolTest {


  private FakeDataSource myDataSource = new FakeDataSource();


  @Before
  public void setUp() throws Exception {
    myDataSource.reset();
  }


  @Test
  public void create() {
    ConnectionPool pool = new ConnectionPool(myDataSource);

    then(pool.isReady()).isFalse();

    pool.disconnect();

    then(pool.isReady()).isFalse();
  }

  @Test
  public void connect_disconnect() throws SQLException {
    ConnectionPool pool = new ConnectionPool(myDataSource);
    pool.connect();

    then(pool.isReady()).isTrue();
    then(pool.countAllConnections()).isEqualTo(1);
    then(pool.countFreeConnections()).isEqualTo(1);
    then(pool.countBorrowedConnections()).isEqualTo(0);

    pool.disconnect();

    then(pool.isReady()).isFalse();
    then(pool.countAllConnections()).isEqualTo(0);
    then(pool.countFreeConnections()).isEqualTo(0);
    then(pool.countBorrowedConnections()).isEqualTo(0);
  }


  @Test
  public void borrow_1() throws SQLException {
    ConnectionPool pool = new ConnectionPool(myDataSource);
    pool.connect();

    Connection connection = pool.borrow();
    then(connection.isValid(1)).isTrue();
    then(connection.isClosed()).isFalse();

    then(pool.countAllConnections()).isEqualTo(1);
    then(pool.countFreeConnections()).isEqualTo(0);
    then(pool.countBorrowedConnections()).isEqualTo(1);

    pool.release(connection);
    then(pool.countAllConnections()).isEqualTo(1);
    then(pool.countFreeConnections()).isEqualTo(1);
    then(pool.countBorrowedConnections()).isEqualTo(0);

    pool.disconnect();

    then(myDataSource.countGotConnections()).isEqualTo(1);
  }


  @Test
  public void borrow_1_two_times() throws SQLException {
    ConnectionPool pool = new ConnectionPool(myDataSource);
    pool.connect();

    Connection connection1 = pool.borrow();
    pool.release(connection1);

    Connection connection2 = pool.borrow();
    pool.release(connection2);

    then(pool.countAllConnections()).isEqualTo(1);

    pool.disconnect();
  }


  @Test
  public void borrow_2() throws SQLException {
    ConnectionPool pool = new ConnectionPool(myDataSource);
    pool.connect();

    Connection connection1 = pool.borrow();

    then(pool.countAllConnections()).isEqualTo(1);
    then(pool.countFreeConnections()).isEqualTo(0);
    then(pool.countBorrowedConnections()).isEqualTo(1);

    Connection connection2 = pool.borrow();

    then(connection2).isNotSameAs(connection1);

    then(pool.countAllConnections()).isEqualTo(2);
    then(pool.countFreeConnections()).isEqualTo(0);
    then(pool.countBorrowedConnections()).isEqualTo(2);

    pool.release(connection1);
    then(pool.countAllConnections()).isEqualTo(2);
    then(pool.countFreeConnections()).isEqualTo(1);
    then(pool.countBorrowedConnections()).isEqualTo(1);

    pool.release(connection2);
    then(pool.countAllConnections()).isEqualTo(2);
    then(pool.countFreeConnections()).isEqualTo(2);
    then(pool.countBorrowedConnections()).isEqualTo(0);

    pool.disconnect();
  }


  @Test
  public void borrow_in_parallel_7_of_10() throws Exception {
    borrow_in_parallel(7, 10);
  }


  @Test
  public void borrow_in_parallel_800_of_33() throws Exception {
    borrow_in_parallel(800, 33);
  }


  private void borrow_in_parallel(int threads, int connectionsLimit) throws Exception {
    final ConnectionPool pool = new ConnectionPool(myDataSource);
    pool.setConnectionsLimit(connectionsLimit);
    pool.connect();

    Callable<Void> task = new Callable<Void>() {
      @Override
      public Void call() throws Exception {

        Connection connection = pool.borrow();
        ThreadTestUtils.sleep(25);
        pool.release(connection);
        return null;

      }
    };

    ThreadTestUtils.parallel(task, threads, 10000);

    then(pool.countAllConnections()).isLessThanOrEqualTo(Math.min(threads, connectionsLimit));
    then(pool.countBorrowedConnections()).isEqualTo(0);

    pool.disconnect();

    then(myDataSource.countGotConnections()).isLessThanOrEqualTo(threads)
                                            .isLessThanOrEqualTo(connectionsLimit);
  }
}