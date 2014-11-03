package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.errors.DBIsNotConnected;
import org.jetbrains.dba.sql.SQL;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;



/**
 * @author Leonid Bushuev from JetBrains
 */
public class BaseFacade implements DBFacade {

  @NotNull
  private final Rdbms myRdbms;

  @NotNull
  private final DataSource myDataSource;

  @NotNull
  protected final SQL mySQL;

  @NotNull
  protected final DBErrorRecognizer myErrorRecognizer;

  private int myConnectionsLimit = 1;

  @Nullable
  protected BaseSession primarySession;


  public BaseFacade(@NotNull final Rdbms rdbms,
                    @NotNull final DataSource source,
                    @NotNull final DBErrorRecognizer recognizer,
                    @NotNull final SQL sql) {
    myRdbms = rdbms;
    myDataSource = source;
    myErrorRecognizer = recognizer;
    mySQL = sql;
  }


  public void setConnectionsLimit(int connectionsLimit) {
    myConnectionsLimit = connectionsLimit;
  }


  @NotNull
  @Override
  public Rdbms rdbms() {
    return myRdbms;
  }


  @NotNull
  public SQL sql() {
    return mySQL;
  }


  public void connect() {
    if (primarySession == null) {
      primarySession = connectAndCreateFacade();
    }
  }

  @NotNull
  protected BaseSession connectAndCreateFacade() {
    Connection connection;
    try {
      connection = myDataSource.getConnection();
    }
    catch (SQLException sqle) {
      throw myErrorRecognizer.recognizeError(sqle, myDataSource.getClass().getSimpleName()+".getConnection()");
    }

    BaseSession session = createFacadeForConnection(connection);
    return session;
  }

  @NotNull
  protected BaseSession createFacadeForConnection(@NotNull final Connection connection) {
    return new BaseSession(this, connection, true);
  }



  public void reconnect() {
    if (primarySession != null) {
      primarySession.close();
    }
    primarySession = connectAndCreateFacade();
  }


  public void disconnect() {
    if (primarySession != null) {
      primarySession.close();
      primarySession = null;
    }
  }


  public boolean isConnected() {
    return primarySession != null;
  }


  public <R> R inTransaction(final InTransaction<R> operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    return primarySession.inTransaction(operation);
  }


  public void inTransaction(final InTransactionNoResult operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    primarySession.inTransaction(operation);
  }


  public <R> R inSession(final InSession<R> operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    return operation.run(primarySession);
  }


  public void inSession(final InSessionNoResult operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    operation.run(primarySession);
  }


  @NotNull
  public DBErrorRecognizer getErrorRecognizer() {
    return myErrorRecognizer;
  }
}
