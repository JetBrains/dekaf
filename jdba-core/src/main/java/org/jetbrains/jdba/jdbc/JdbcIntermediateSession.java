package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ParameterDef;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.DBSessionIsClosedException;
import org.jetbrains.jdba.intermediate.DBExceptionRecognizer;
import org.jetbrains.jdba.intermediate.IntegralIntermediateSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;



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
                new LinkedBlockingQueue<JdbcIntermediateSeance>(4);


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
  public <I> I getSpecificService(@NotNull final Class<I> serviceInterface,
                                  @NotNull final String name) {
    if (name.equalsIgnoreCase("jdbc-connection")) {
      try {
        if (serviceInterface.isAssignableFrom(myConnection.getClass())) {
          //noinspection unchecked
          return (I) myConnection;
        }
        else if (myConnection.isWrapperFor(serviceInterface)) {
          return myConnection.unwrap(serviceInterface);
        }
        else {
          return null;
        }
      }
      catch (SQLException sqle) {
        throw recognizeException(sqle, "getting specific service "+name);
      }
    }
    else if (name.equals("inter-session")) {
      if (serviceInterface.isAssignableFrom(this.getClass())) {
        //noinspection unchecked
        return (I) this;
      }
      else {
        return null;
      }
    }
    else {
      throw new IllegalArgumentException("Unknown specific service: " + name);
    }
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
