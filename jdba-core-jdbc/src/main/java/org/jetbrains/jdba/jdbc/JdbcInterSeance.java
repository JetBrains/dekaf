package org.jetbrains.jdba.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jdba.core.DBInterCursor;
import org.jetbrains.jdba.core.DBInterSeance;
import org.jetbrains.jdba.core.ResultLayout;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class JdbcInterSeance implements DBInterSeance {

  //// SOME CONSTANTS \\\\

  protected static final boolean EXECUTE_RETURNS_CURSOR = true;
  protected static final boolean EXECUTE_RETURNS_AFFECTED_ROWS_COUNT = false;


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


  //// CONSTRUCTORS \\\\


  protected JdbcInterSeance(@NotNull final JdbcInterSession session, @NotNull final String statementText) {
    this.session = session;
    this.statementText = statementText;
  }


  //// IMPLEMENTATION \\\\



  @Override
  public void setInParameters(@NotNull final Object[] parameters) {
    // TODO implement JdbcInterSeance.setInParameters()
    throw new RuntimeException("Method JdbcInterSeance.setInParameters() is not implemented yet.");

  }


  @Override
  public void execute() {
    try {
      boolean gotResultSet = statement.execute();
      if (gotResultSet)  {
        myDefaultResultSet = statement.getResultSet();
        myDefaultResultSetHasRows = myDefaultResultSet.next();  // download first rows
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

  @NotNull
  @Override
  public <R> DBInterCursor<R> openCursor(final int parameterPosition,
                                         @NotNull final ResultLayout<R> layout) {
    // TODO implement JdbcInterSeance.openCursor()
    throw new RuntimeException("Method JdbcInterSeance.openCursor() is not implemented yet.");

  }

  @Override
  public void close() {
    if (myDefaultResultSet != null) {
      try {
        myDefaultResultSet.close();
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
        statement.close();
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
