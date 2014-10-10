package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.errors.DBIsNotConnected;
import org.jetbrains.dba.sql.SQL;
import org.jetbrains.dba.utils.Version;

import java.sql.Driver;



/**
 *
 **/
public abstract class BaseFacade implements DBFacade {

  @NotNull
  protected final String myConnectionString;

  @NotNull
  protected final SQL mySQL;

  @NotNull
  protected final BaseErrorRecognizer myErrorRecognizer;

  @Nullable
  protected BaseSession primarySession;


  protected BaseFacade(@NotNull String connectionString, @NotNull SQL sql, @NotNull BaseErrorRecognizer recognizer) {
    this.myConnectionString = connectionString;
    mySQL = sql;
    myErrorRecognizer = recognizer;
  }


  @NotNull
  protected abstract Driver getDriver();


  @NotNull
  public SQL sql() {
    return mySQL;
  }


  @NotNull
  @Override
  public Version getDriverVersion() {
    Driver driver = getDriver();
    return Version.of(driver.getMajorVersion(), driver.getMinorVersion());
  }


  @Override
  public void connect() {
    if (primarySession == null) {
      primarySession = internalConnect();
    }
    else {
      //noinspection StatementWithEmptyBody
      if (this.myConnectionString.equals(myConnectionString)) {
        // already connected to the same URL
      }
      else {
        throw new IllegalStateException("Could not connect to another URL");
      }
    }
  }


  protected abstract BaseSession internalConnect();


  @Override
  public void reconnect() {
    if (primarySession != null) {
      primarySession.close();
    }
    primarySession = internalConnect();
  }


  @Override
  public void disconnect() {
    if (primarySession != null) {
      primarySession.close();
    }
    primarySession = null;
  }


  @Override
  public boolean isConnected() {
    return primarySession != null;
  }


  @Override
  public <R> R inTransaction(final InTransaction<R> operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    return primarySession.inTransaction(operation);
  }


  @Override
  public void inTransaction(final InTransactionNoResult operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    primarySession.inTransaction(operation);
  }


  @Override
  public <R> R inSession(final InSession<R> operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    return operation.run(primarySession);
  }


  @Override
  public void inSession(final InSessionNoResult operation) {
    if (primarySession == null) {
      throw new DBIsNotConnected("Facade is not connected.");
    }

    operation.run(primarySession);
  }


  @NotNull
  public BaseErrorRecognizer getErrorRecognizer() {
    return myErrorRecognizer;
  }
}
