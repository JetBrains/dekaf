package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;



/**
 * Oracle session.
 */
final class OraSession extends DatabaseAbstractSession {

  public OraSession(@NotNull OraFacade facade, @NotNull final Connection connection, final boolean ownConnection) {
    super(facade, connection, ownConnection);
  }
}
