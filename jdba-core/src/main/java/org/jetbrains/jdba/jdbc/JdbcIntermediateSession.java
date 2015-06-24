package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ParameterDef;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.DBSessionIsClosedException;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSession;

import java.sql.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.jetbrains.jdba.util.Objects.castTo;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateSession implements IntegralIntermediateSession {

  @Nullable
  private final JdbcIntermediateFacade myFacade;

  @NotNull
  private final DBExceptionRecognizer myExceptionRecognizer;

  @NotNull
  private final Connection myConnection;

  private final boolean myOwnConnection;

  private boolean myInTransaction;

  private boolean myClosed;

  @NotNull
  private final Queue<JdbcIntermediateSeance> mySeances =
                new LinkedBlockingQueue<JdbcIntermediateSeance>();


  protected JdbcIntermediateSession(@Nullable final JdbcIntermediateFacade facade,
                                    @NotNull final DBExceptionRecognizer exceptionRecognizer,
                                    @NotNull final Connection connection,
                                    final boolean ownConnection) {
    myFacade = facade;
    myExceptionRecognizer = exceptionRecognizer;
    myConnection = connection;
    myOwnConnection = ownConnection;
    myClosed = false;
  }


  @Override
  public synchronized void beginTransaction() {
    checkNotClosed();

    if (!myInTransaction) {
      try {
        myConnection.setAutoCommit(false);
      }
      catch (SQLException sqle) {
        throw recognizeException(sqle, "set autocommit = false");
      }
    }
    else {
      throw new IllegalStateException("The sessions is already in a transaction");
    }

    myInTransaction = true;
  }

  @Override
  public synchronized void commit() {
    if (myInTransaction) {
      myInTransaction = false;
      try {
        myConnection.commit();
      }
      catch (SQLException sqle) {
        rollback();
        throw myExceptionRecognizer.recognizeException(sqle, "commit");
      }
    }
    else {
      throw new IllegalStateException("The sessions is not in a transaction");
    }
  }

  @Override
  public synchronized void rollback() {
    if (myClosed) return;

    // TODO close all seances here

    myInTransaction = false;

    try {
      myConnection.rollback();
      myConnection.setAutoCommit(true);
    }
    catch (SQLException sqle) {
      // TODO log it somehow
      close(); // this connection is broken
    }
  }

  @NotNull
  @Override
  public synchronized JdbcIntermediateSeance openSeance(@NotNull final String statementText,
                                                        @Nullable final ParameterDef[] outParameterDefs) {
    checkNotClosed();

    final JdbcIntermediateSeance seance;
    if (outParameterDefs == null) {
      seance = openSimpleStatementSeance(statementText);
    }
    else {
      seance = openPreparedStatementSeance(statementText, outParameterDefs);
    }

    mySeances.add(seance);
    return seance;
  }

  @NotNull
  protected JdbcIntermediateSimpleSeance openSimpleStatementSeance(@NotNull final String statementText) {
    return new JdbcIntermediateSimpleSeance(this, statementText);
  }

  @NotNull
  protected JdbcIntermediateCallableSeance openPreparedStatementSeance(@NotNull final String statementText,
                                                                    @NotNull final ParameterDef[] outParameterDefs) {
    return new JdbcIntermediateCallableSeance(this, statementText, outParameterDefs);
  }


  @Override
  public <I> I getSpecificService(@NotNull final Class<I> serviceClass,
                                  @NotNull final String serviceName) {
    if (serviceName.equalsIgnoreCase(Names.INTERMEDIATE_SERVICE)) {
      return castTo(serviceClass, this);
    }
    else if (serviceName.equalsIgnoreCase(Names.JDBC_CONNECTION)) {
      return castTo(serviceClass, myConnection);
    }
    else {
      return null;
    }
  }


  @Override
  public long ping() {
    if (myClosed) throw new DBSessionIsClosedException("The session is closed.");

    long time1 = System.currentTimeMillis();

    boolean ok = false;
    try {
      if (myConnection.isClosed()) throw new DBSessionIsClosedException("The JDBC connection is closed.");

      final Statement statement = myConnection.createStatement();
      try {
        performPing(statement);
        ok = true;
      }
      finally {
        statement.close();
      }
    }
    catch (SQLException sqle) {
      throw myExceptionRecognizer.recognizeException(sqle, "ping");
    }
    finally {
      if (!ok) close();
    }

    long duration = System.currentTimeMillis() - time1;
    if (duration == 0L) duration = 1L;
    return duration;
  }

  protected void performPing(final Statement statement) throws SQLException {
    final String pingQuery = "select 1";
    statement.execute(pingQuery);
  }


  @Override
  public synchronized void close() {
    if (myClosed) return;

    closeSeances();

    if (myInTransaction) {
      rollback();
    }

    myClosed = true;

    if (myFacade != null) {
      myFacade.sessionIsClosed(this, myConnection);
    }

    if (myOwnConnection) {
      try {
        myConnection.close();
      }
      catch (SQLException sqle) {
        // TODO log it somehow
      }
    }
  }

  private void closeSeances() {
    while (!mySeances.isEmpty()) {
      JdbcIntermediateSeance seanceToClose = mySeances.poll();
      seanceToClose.close();
    }
  }

  @Override
  public boolean isClosed() {
    return myClosed;
  }


  //// INTERNAL METHODS TO WORK WITH JDBC \\\\

  static final int DEFAULT_FETCH_SIZE = 1000;


  @NotNull
  PreparedStatement prepareSimpleStatement(@NotNull final String statementText)
      throws SQLException
  {
    PreparedStatement statement = myConnection.prepareStatement(statementText,
                                                                ResultSet.TYPE_FORWARD_ONLY,
                                                                ResultSet.CONCUR_READ_ONLY);
    statement.setFetchSize(DEFAULT_FETCH_SIZE);
    return statement;
  }

  @NotNull
  CallableStatement prepareCallableStatement(@NotNull final String statementText)
      throws SQLException
  {
    return myConnection.prepareCall(statementText);
  }

  @NotNull
  ResultSet getDefaultResultSet(@NotNull final Statement statement) throws SQLException {
    return statement.getResultSet();
  }

  void tuneResultSet(@NotNull final ResultSet rset) throws SQLException {
    if (rset.getFetchDirection() != ResultSet.FETCH_FORWARD) {
      rset.setFetchDirection(ResultSet.FETCH_FORWARD);
    }

    rset.setFetchSize(DEFAULT_FETCH_SIZE);
  }




  //// DIAGNOSTIC METHODS \\\\

  public int countOpenedSeances() {
    int count = 0;
    for (JdbcIntermediateSeance seance : mySeances) if (seance.isStatementOpened()) count++;
    return count;
  }

  public int countOpenedCursors() {
    int count = 0;
    for (JdbcIntermediateSeance seance : mySeances) count += seance.countOpenedCursors();
    return count;
  }



  //// INTERNAL METHODS \\\\

  @NotNull
  protected Connection getConnection() {
    return myConnection;
  }



  //// USEFUL METHODS \\\\

  private void checkNotClosed() {
    if (myClosed) throw new DBSessionIsClosedException("The session is closed");
  }

  @NotNull
  protected DBException recognizeException(@NotNull final SQLException sqle) {
    return myExceptionRecognizer.recognizeException(sqle, null);
  }

  @NotNull
  protected DBException recognizeException(@NotNull final SQLException sqle,
                                           @Nullable final String statementText) {
    return myExceptionRecognizer.recognizeException(sqle, statementText);
  }


}
