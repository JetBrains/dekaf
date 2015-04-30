package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBInterCursor;
import org.jetbrains.jdba.core.ResultLayout;

import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class JdbcInterCursor<R> implements DBInterCursor<R> {

  @NotNull
  private final JdbcInterSeance mySeance;

  @NotNull
  private final ResultSet myResultSet;

  @NotNull
  protected final ResultLayout<R> myResultLayout;

  private final boolean myIsDefault;

  private boolean myOpened;

  private boolean myHasRows;

  private int myFetchLimit = 1000000;


  protected JdbcInterCursor(@NotNull final JdbcInterSeance seance,
                            @NotNull final ResultSet resultSet,
                            @NotNull final ResultLayout<R> resultLayout,
                            final boolean isDefault,
                            @Nullable final Boolean hasRows) {
    mySeance = seance;
    myResultSet = resultSet;
    myResultLayout = resultLayout;
    myIsDefault = isDefault;

    try {
      myOpened = myResultSet.isClosed();
    }
    catch (SQLException sqle) {
      throw seance.session.recognizeException(sqle, seance.statementText);
    }

    if (myOpened) {
      if (hasRows == null) {
        try {
          myHasRows = resultSet.next(); // attempt to get the first portion of rows
        }
        catch (SQLException sqle) {
          throw seance.session.recognizeException(sqle, seance.statementText);
        }
      }
      else {
        myHasRows = hasRows;
      }
    }
    else {
      myHasRows = false;
    }
  }


  @Override
  public boolean hasRows() {
    return myHasRows;
  }


  @NotNull
  @Override
  public String[] getColumnNames() {
    if (!myOpened) throw new IllegalStateException("The cursor is not opened or is yet closed.");

    // TODO implement JdbcInterCursor.getColumnNames()
    throw new RuntimeException("Method JdbcInterCursor.getColumnNames() is not implemented yet.");

  }

  @Override
  public synchronized void setFetchLimit(final int limit) {
    myFetchLimit = limit;
  }

  @Override
  public R fetch() {
    if (!myOpened) throw new IllegalStateException("The cursor is not opened or is yet closed.");

    // TODO implement JdbcInterCursor.fetch()
    throw new RuntimeException("Method JdbcInterCursor.fetch() is not implemented yet.");

  }

  boolean isOpened() {
    return myOpened;
  }

  @Override
  public synchronized void close() {
    try {
      if (!myResultSet.isClosed()) {
        myResultSet.close();
      }
    }
    catch (SQLException e) {
      // TODO log somehow
    }
    finally {
      myHasRows = false;
      myOpened = false;
    }
  }
}
