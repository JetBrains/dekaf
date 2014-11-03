package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.sql.SQL;

import javax.sql.DataSource;
import java.sql.Connection;



/**
 * PostgreSQL facade.
 */
public final class PostgreFacade extends BaseFacade {

  public PostgreFacade(@NotNull Rdbms rdbms,
                       @NotNull DataSource source,
                       @NotNull DBErrorRecognizer recognizer, @NotNull SQL sql) {
    super(rdbms, source, recognizer, sql);
  }


  @NotNull
  @Override
  protected PostgreSession createFacadeForConnection(@NotNull Connection connection) {
    return new PostgreSession(this, connection, true);
  }

}
