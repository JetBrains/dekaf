package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.intermediate.DBErrorRecognizer;
import org.jetbrains.jdba.intermediate.IntegralIntermediateFacade;
import org.jetbrains.jdba.jdbc.pooling.ConnectionPool;
import org.jetbrains.jdba.jdbc.pooling.SimpleDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateFacade implements IntegralIntermediateFacade {

  //// STATE \\\\

  @NotNull
  protected final ConnectionPool myPool;

  @NotNull
  protected final DBErrorRecognizer myErrorRecognizer;

  private final LinkedBlockingQueue<JdbcIntermediateSession> mySessions =
          new LinkedBlockingQueue<JdbcIntermediateSession>();


  //// CONSTRUCTORS \\\\

  public JdbcIntermediateFacade(@NotNull final String connectionString,
                                @Nullable final Properties connectionProperties,
                                @NotNull final Driver driver,
                                int connectionsLimit,
                                @NotNull final DBErrorRecognizer errorRecognizer) {
    this(prepareDataSource(connectionString, connectionProperties, driver), connectionsLimit, errorRecognizer);
  }

  public JdbcIntermediateFacade(@NotNull final DataSource dataSource,
                                int connectionsLimit,
                                @NotNull final DBErrorRecognizer errorRecognizer) {
    myPool = new ConnectionPool(dataSource);
    myPool.setConnectionsLimit(connectionsLimit);
    myErrorRecognizer = errorRecognizer;
  }

  @NotNull
  private static DataSource prepareDataSource(final @NotNull String connectionString,
                                              final @Nullable Properties connectionProperties,
                                              final @NotNull Driver driver) {
    final SimpleDataSource dataSource = new SimpleDataSource(connectionString,
                                                             connectionProperties,
                                                             driver);
    dataSource.setLogWriter(new PrintWriter(System.out));
    return dataSource;
  }


  //// IMPLEMENTATION \\\\

  @NotNull
  @Override
  public Rdbms rdbms() {
    return UnknownDatabase.RDBMS;
  }


  @Override
  public void connect() {
    try {
      myPool.connect();
    }
    catch (SQLException sqle) {
      throw  myErrorRecognizer.recognizeError(sqle, "connect");
    }
  }

  @Override
  public void reconnect() {
    // TODO implement JdbcInterFacade.reconnect
    throw new RuntimeException("The JdbcInterFacade.reconnect has not been implemented yet.");
  }

  @Override
  public void disconnect() {
    try {
      while (!mySessions.isEmpty()) {
        Thread.sleep(10);
        //noinspection MismatchedQueryAndUpdateOfCollection
        Collection<JdbcIntermediateSession> sessionsToClose = new ArrayList<JdbcIntermediateSession>(10);
        mySessions.drainTo(sessionsToClose, 10);
        for (JdbcIntermediateSession sessionToClose : sessionsToClose) {
          sessionToClose.close();
        }
      }
    }
    catch (InterruptedException ie) {
      // do nothing
    }

    myPool.disconnect();
  }

  @Override
  public boolean isConnected() {
    return myPool.isReady();
  }

  @NotNull
  @Override
  public JdbcIntermediateSession openSession() {
    final Connection connection;
    try {
      connection = myPool.borrow();
    }
    catch (SQLException sqle) {
      throw  myErrorRecognizer.recognizeError(sqle, "borrow a connection from the pool");
    }

    final JdbcIntermediateSession session =
            new JdbcIntermediateSession(this, myErrorRecognizer, connection, false);
    mySessions.add(session);
    return session;
  }

  @NotNull
  public DBErrorRecognizer getErrorRecognizer() {
    return myErrorRecognizer;
  }

  void sessionIsClosed(@NotNull final JdbcIntermediateSession session, @NotNull final Connection connection) {
    mySessions.remove(session);
    myPool.release(connection);
  }


  //// DIAGNOSTIC METHODS \\\\

  public int countOpenedSessions() {
    return mySessions.size();
  }

  public int countOpenedConnections() {
    return myPool.countAllConnections();
  }

}
