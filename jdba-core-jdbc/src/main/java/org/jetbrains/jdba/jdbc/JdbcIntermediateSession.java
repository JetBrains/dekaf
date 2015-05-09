package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ParameterDef;
import org.jetbrains.jdba.exceptions.DBException;
import org.jetbrains.jdba.exceptions.DBSessionIsClosedException;
import org.jetbrains.jdba.intermediate.DBErrorRecognizer;
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
  private final DBErrorRecognizer myErrorRecognizer;

  @NotNull
  private final Connection myConnection;

  private final boolean myOwnConnection;

  private boolean myInTransaction;

  private boolean myClosed;

  @NotNull
  private final Queue<JdbcIntermediateSeance> mySeances =
          new LinkedBlockingQueue<JdbcIntermediateSeance>(4);


  protected JdbcIntermediateSession(@Nullable final JdbcIntermediateFacade facade,
                                    @NotNull final DBErrorRecognizer errorRecognizer,
                                    @NotNull final Connection connection,
                                    final boolean ownConnection) {
    myFacade = facade;
    myErrorRecognizer = errorRecognizer;
    myConnection = connection;
    myOwnConnection = ownConnection;
    myClosed = false;
  }


  @Override
  public void beginTransaction() {
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
  public void commit() {
    if (myInTransaction) {
      myInTransaction = false;
      try {
        myConnection.commit();
      }
      catch (SQLException sqle) {
        rollback();
        throw myErrorRecognizer.recognizeError(sqle, "commit");
      }
    }
    else {
      throw new IllegalStateException("The sessions is not in a transaction");
    }
  }

  @Override
  public void rollback() {
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
  public JdbcIntermediateSeance openSeance(@NotNull final String statementText,
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
  public void close() {
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
    return myErrorRecognizer.recognizeError(sqle, null);
  }

  @NotNull
  protected DBException recognizeException(@NotNull final SQLException sqle,
                                           @Nullable final String statementText) {
    return myErrorRecognizer.recognizeError(sqle, statementText);
  }


}
