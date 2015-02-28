package org.jetbrains.dba.rdbms.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.access.BaseSession;

import java.sql.Connection;



/**
 * MySQL session.
 */
final class MysqlSession extends BaseSession {

  public MysqlSession(@NotNull MysqlFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }

}
