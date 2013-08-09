package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;



/**
 * MySQL session.
 */
final class MysqlSession extends BaseSession {

  public MysqlSession(@NotNull MysqlFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }
}
