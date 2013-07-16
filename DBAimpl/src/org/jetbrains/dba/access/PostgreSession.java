package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;



/**
 * PostgreSQL session.
 */
final class PostgreSession extends BaseSession {

  public PostgreSession(@NotNull PostgreFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }
}
