package org.jetbrains.jdba.rdbms.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.BaseSession;

import java.sql.Connection;



/**
 * MySQL session.
 */
final class MysqlSession extends BaseSession {

  public MysqlSession(@NotNull MysqlFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }

}
