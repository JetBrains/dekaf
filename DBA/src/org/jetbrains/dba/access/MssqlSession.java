package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;



/**
 * MS SQL Server session.
 */
final class MssqlSession extends BaseSession {

  public MssqlSession(@NotNull MssqlFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }
}
