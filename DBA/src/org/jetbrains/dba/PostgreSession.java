package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;



/**
 * Oracle session.
 */
final class PostgreSession extends DatabaseAbstractSession {

  public PostgreSession(@NotNull PostgreFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }
}
