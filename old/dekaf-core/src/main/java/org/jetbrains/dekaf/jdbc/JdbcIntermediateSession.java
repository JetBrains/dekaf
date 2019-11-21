package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.core.ParameterDef;
import org.jetbrains.dekaf.exceptions.DBException;
import org.jetbrains.dekaf.exceptions.DBSessionIsClosedException;
import org.jetbrains.dekaf.intermediate.DBExceptionRecognizer;
import org.jetbrains.dekaf.intermediate.IntegralIntermediateSession;

import java.sql.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Math.min;
import static org.jetbrains.dekaf.core.Layouts.arrayOf;
import static org.jetbrains.dekaf.core.Layouts.rowOf;
import static org.jetbrains.dekaf.util.Objects.castTo;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcIntermediateSession implements IntegralIntermediateSession {

  @Nullable
  protected final JdbcIntermediateFacade myFacade;

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
  public boolean isInTransaction() {
    return myInTransaction;
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
      JdbcUtil.printOperationException(sqle, "rollback");
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
    else if (serviceName.equalsIgnoreCase(Names.JDBC_METADATA)) {
      return castTo(serviceClass, getConnectionMetaData());
    }
    else {
      return null;
    }
  }

  private DatabaseMetaData getConnectionMetaData() {
    try {
      return myConnection.getMetaData();
    }
    catch (SQLException e) {
      String className = myConnection.getClass().getName();
      throw myExceptionRecognizer.recognizeException(e, className+".getMetaData()");
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
      JdbcUtil.close(myConnection);
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

  protected static final int DEFAULT_FETCH_SIZE = 1000;
  protected static final int LARGEST_FETCH_SIZE = 1000000;


  @NotNull
  PreparedStatement prepareSimpleStatement(@NotNull final String statementText)
      throws SQLException
  {
    PreparedStatement statement = myConnection.prepareStatement(statementText,
                                                                ResultSet.TYPE_FORWARD_ONLY,
                                                                ResultSet.CONCUR_READ_ONLY);
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

  void tuneStatement(@NotNull final PreparedStatement stmt, final int packLimit) throws SQLException {
    tuneStatementWithFetchSize(stmt, packLimit);
  }

  protected void tuneStatementWithFetchSize(final PreparedStatement stmt, final int packLimit) throws SQLException {
    if (packLimit > 0) {
      int fetchSize = min(packLimit, LARGEST_FETCH_SIZE);
      stmt.setFetchSize(fetchSize);
    }
    else {
      stmt.setFetchSize(getDefaultFetchSize());
    }

  }

  void tuneResultSet(@NotNull final ResultSet rset, final int packLimit) throws SQLException {
    tuneResultSetWithFetchSize(rset, packLimit);
  }

  protected void tuneResultSetWithFetchSize(@NotNull final ResultSet rset, int packLimit) throws SQLException {
    if (JdbcUtil.getFetchDirection(rset) != ResultSet.FETCH_FORWARD) {
      rset.setFetchDirection(ResultSet.FETCH_FORWARD);
    }

    if (packLimit > 0) {
      int fetchSize = min(packLimit, LARGEST_FETCH_SIZE);
      rset.setFetchSize(fetchSize);
    }
    else {
      rset.setFetchSize(getDefaultFetchSize());
    }
  }


  protected int getDefaultFetchSize() {
    return DEFAULT_FETCH_SIZE;
  }


  /**
   * A simple internal function that performs a simple query without parameters
   * and returns the first row as an array.
   * @param queryText   the query text.
   * @param columnCount count of columns.
   * @param columnClass class of columns.
   * @param <V>         type of columns.
   * @return            the first row, or null if no rows.
   */
  @Nullable
  public <V> V[] queryOneRow(@NotNull final String queryText,
                             final int columnCount,
                             @NotNull final Class<V> columnClass) {
    JdbcIntermediateSeance seance1 = this.openSeance(queryText, null);
    try {
      seance1.execute();
      JdbcIntermediateCursor<V[]> cursor1 =
          seance1.openDefaultCursor(rowOf(arrayOf(columnCount, columnClass)));
      try {
        if (cursor1.hasRows()) return cursor1.fetch();
        else                   return null;
      }
      finally {
        cursor1.close();
      }
    }
    finally {
      seance1.close();
    }
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
