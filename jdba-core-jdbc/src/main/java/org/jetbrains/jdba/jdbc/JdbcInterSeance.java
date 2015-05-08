package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.ResultLayout;
import org.jetbrains.jdba.inter.DBInterSeance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcInterSeance implements DBInterSeance {


  //// STATE \\\\

  @NotNull
  protected final JdbcInterSession session;

  @NotNull
  protected final String statementText;

  protected PreparedStatement statement;

  @Nullable
  protected ResultSet myDefaultResultSet;

  protected boolean myDefaultResultSetHasRows;

  protected int myAffectedRowsCount;

  @Nullable
  protected JdbcInterCursor<?> myDefaultCursor;


  //// CONSTRUCTORS \\\\


  protected JdbcInterSeance(@NotNull final JdbcInterSession session, @NotNull final String statementText) {
    this.session = session;
    this.statementText = statementText;
  }


  //// IMPLEMENTATION \\\\



  @Override
  public synchronized void setInParameters(@NotNull final Object[] parameters) {
    // TODO implement JdbcInterSeance.setInParameters()
    throw new RuntimeException("Method JdbcInterSeance.setInParameters() is not implemented yet.");

  }


  @Override
  public synchronized void execute() {
    try {
      boolean gotResultSet = statement.execute();
      if (gotResultSet)  {
        myDefaultResultSet = statement.getResultSet();
        myDefaultResultSetHasRows = myDefaultResultSet.next();  // download first rows
        if (!myDefaultResultSetHasRows)  {
          myDefaultResultSet.close(); // close it if it has no rows
        }
      }
      else {
        myAffectedRowsCount = statement.getUpdateCount();
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int getAffectedRowsCount() {
    return myAffectedRowsCount;
  }


  protected <R> JdbcInterCursor<R> openDefaultCursor(final @NotNull ResultLayout<R> layout) {
    if (myDefaultResultSet == null) {
      throw new IllegalStateException("Cannot open cursor: the statement was not executed or it has not returned cursor.");
    }

    if (myDefaultCursor == null) {
      JdbcInterCursor<R> cursor = new JdbcInterCursor<R>(this, myDefaultResultSet, layout,
                                                         true, myDefaultResultSetHasRows ? Boolean.TRUE : Boolean.FALSE);
      myDefaultCursor = cursor;
      return cursor;
    }
    else {
      if (layout.equals(myDefaultCursor.myResultLayout)) {
        //noinspection unchecked
        return (JdbcInterCursor<R>) myDefaultCursor;
      }
      else {
        throw new IllegalStateException("The cursor already opened with another layout.");
      }
    }
  }


  @Override
  public synchronized void close() {
    if (myDefaultCursor != null && myDefaultCursor.isOpened()) {
      myDefaultCursor.close();
      myDefaultCursor = null;
    }

    if (myDefaultResultSet != null) {
      try {
        if (!myDefaultResultSet.isClosed()) {
          myDefaultResultSet.close();
        }
      }
      catch (SQLException sqle) {
        // TODO log somehow
      }
      finally {
        myDefaultResultSet = null;
      }
    }

    if (statement != null) {
      try {
        if (!statement.isClosed()) {
          statement.close();
        }
      }
      catch (SQLException sqle) {
        // TODO log somehow
      }
      finally {
        statement = null;
      }
    }
  }
}
