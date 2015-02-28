package org.jetbrains.dba.rdbms.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.core.BaseSession;

import java.sql.Connection;



/**
 * PostgreSQL session.
 */
final class PostgreSession extends BaseSession {

  public PostgreSession(@NotNull PostgreFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }

}
