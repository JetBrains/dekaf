package org.jetbrains.dba.jdbc.pooling;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;



/**
 * A small pool of JDBC connections.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class ConnectionPool {

  @NotNull
  private final DataSource myOriginalSource;

  private final CopyOnWriteArrayList<Connection> myAllConnections = new CopyOnWriteArrayList<Connection>();

  private final LinkedBlockingDeque<Connection> myFreeConnections = new LinkedBlockingDeque<Connection>();


  private volatile int myHoldConnections = 1;
  private volatile int myConnectionsLimit = 10;

  private int myBorrowTimeBeforeReplenish = 70;
  private int myBorrowTimeOut = 60000;

  private volatile boolean myReady;




  public ConnectionPool(@NotNull DataSource originalSource) {
    myOriginalSource = originalSource;
  }


  //// MAIN ACTIVITY: CONNECT, BORROW, RELEASE, DISCONNECT \\\\


  public void connect()
    throws SQLException
  {
    int n = myHoldConnections - myAllConnections.size();
    if (myAllConnections.isEmpty()) n = Math.max(n, 1);
    for (int i = 0; i < n; i++) {
      obtainOneConnectionIntoPool();
    }

    myReady = !myAllConnections.isEmpty();
  }


  private synchronized void obtainOneConnectionIntoPool() throws SQLException {
    // check whether we can obtain one more
    if (myAllConnections.size() >= myConnectionsLimit) {
      return; // :(
    }

    // obtain from the source
    Connection connection = myOriginalSource.getConnection();
    if (connection == null) throw new UnexpectedDataSourceException("DataSource " + myOriginalSource.getClass().getName() + " returned null.", "<DataSource.getConnection()>");

    // prepare this connection
    try {
      prepareConnectionAfterConnected(connection);
    }
    catch (SQLException sqle) {
      closeConnection(connection);
      throw sqle;
    }

    // ok
    myAllConnections.add(connection);
    if (myReady) sleep(11);
    myFreeConnections.offer(connection);
  }


  protected void prepareConnectionAfterConnected(@NotNull final Connection connection)
    throws SQLException
  {
    connection.setAutoCommit(true);
  }


  public boolean isReady() {
    return myReady;
  }


  @NotNull
  public Connection borrow() throws SQLException {
    if (!myReady) throw new ConnectionPoolIsNotReadyException("The connection pool is not connected yet or may be is disconnecting.");

    Connection connection = provideWithConnection();
    try {
      activateConnection(connection);
    }
    catch (SQLException e) {
      // TODO
      throw e;
    }

    return connection;
  }


  @NotNull
  private Connection provideWithConnection() throws SQLException {
    try {

      // try to get already obtained connection
      @Nullable
      Connection connection = myFreeConnections.poll(myBorrowTimeBeforeReplenish, TimeUnit.MILLISECONDS);
      if (connection != null) return connection;

      // check whether we can obtain a new one
      while (myAllConnections.size() < myConnectionsLimit) {
        obtainOneConnectionIntoPool();
        connection = myFreeConnections.poll(myBorrowTimeBeforeReplenish, TimeUnit.MILLISECONDS);
        if (connection != null) return connection;
      }

      // wait for one is released by another thread
      connection = myFreeConnections.poll(myBorrowTimeOut, TimeUnit.MILLISECONDS);
      if (connection != null) return connection;

      // we have no luck :(
      int n = myAllConnections.size();
      throw new ConnectionPoolExhaustedException("The Connection Pool exhausted: all "+n+" connections are borrowed and not returned yet.");
    }
    catch (InterruptedException ie) {
      throw new ConnectionPoolOperationInterruptedException("Operation interrupted", ie, "<provide with connection>");
    }
  }


  protected void activateConnection(@NotNull final Connection connection) throws SQLException {}


  public void release(@NotNull final Connection connection) {
    try {
      passivateConnection(connection);
      myFreeConnections.offerFirst(connection);
    }
    catch (Exception e) {
      panic("Passivate Connection", e);
      closeAndLeaveConnection(connection);
    }
  }


  protected void passivateConnection(@NotNull final Connection connection) throws SQLException {
    if (!connection.getAutoCommit()) {
      connection.rollback();
      connection.setAutoCommit(true);
    }
  }


  public void disconnect() {
    myReady = false;

    while (!myAllConnections.isEmpty()) {
      sleep(11);
      //noinspection MismatchedQueryAndUpdateOfCollection
      final Collection<Connection> toClose = new ArrayList<Connection>(myConnectionsLimit);
      myFreeConnections.drainTo(toClose);
      myAllConnections.removeAll(toClose);
      for (Connection connection : toClose) closeConnection(connection);
      toClose.clear();
    }
  }


  private void closeAndLeaveConnection(@NotNull final Connection connection) {
    myFreeConnections.remove(connection);
    myAllConnections.remove(connection);
    closeConnection(connection);
    myReady = myReady && !myAllConnections.isEmpty();
  }


  private static void closeConnection(final Connection connection) {
    try {
      connection.close();
    }
    catch (Exception e) {
      panic("Close connection", e);
    }
  }


  //// TUNING \\\\


  public int getHoldConnections() {
    return myHoldConnections;
  }


  public void setHoldConnections(int holdConnections) {
    myHoldConnections = holdConnections;
  }


  public int getConnectionsLimit() {
    return myConnectionsLimit;
  }


  public void setConnectionsLimit(int connectionsLimit) {
    myConnectionsLimit = connectionsLimit;
  }


  //// STATISTICS \\\\


  public int countAllConnections() {
    return myAllConnections.size();
  }

  public int countFreeConnections() {
    return myFreeConnections.size();
  }

  public int countBorrowedConnections() {
    int a = myAllConnections.size(),
        f = myFreeConnections.size(),
        z;
    do {
      z = a - f;
    } while (!(a == myAllConnections.size() && f == myFreeConnections.size()));
    return z;
  }


  //// USEFUL INTERNAL METHODS \\\\


  private static void sleep(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    }
    catch (InterruptedException e) {
      // do nothing here
    }
  }

  private static void panic(final String operationDescription, final Exception e) {
    System.err.printf("Operation %s failed: %s: %s\n",
                      operationDescription, e.getClass().getSimpleName(), e.getMessage());
  }




}
