package org.jetbrains.jdba.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.BaseSession;

import java.sql.Connection;



/**
 * PostgreSQL session.
 */
final class PostgreSession extends BaseSession {

  public PostgreSession(@NotNull PostgreFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }

}
