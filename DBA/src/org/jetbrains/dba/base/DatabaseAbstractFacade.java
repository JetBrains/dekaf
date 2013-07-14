package org.jetbrains.dba.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dba.errors.DBIsNotConnected;



/**
 *
 **/
public abstract class DatabaseAbstractFacade implements DBFacade {

  @Nullable
  protected String jdbcURL;

  @Nullable
  protected DatabaseAbstractSession primarySession;


  @Override
  public void connect(@NotNull final String jdbcURL) {
    if (primarySession == null) {
      this.jdbcURL = jdbcURL;
      primarySession = internalConnect(jdbcURL);
    }
    else {
      if (this.jdbcURL.equals(jdbcURL)) {
        ; // already connected to the same URL
      }
      else {
        throw new IllegalStateException("Could not connect to another URL");
      }
    }
  }


  protected abstract DatabaseAbstractSession internalConnect(@NotNull final String jdbcURL);


  @Override
  public void reconnect() {
    if (primarySession != null) {
      primarySession.close();
    }
    primarySession = internalConnect(jdbcURL);
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
}
