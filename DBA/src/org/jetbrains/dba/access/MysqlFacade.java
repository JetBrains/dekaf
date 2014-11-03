package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.Rdbms;
import org.jetbrains.dba.sql.SQL;

import javax.sql.DataSource;
import java.sql.Connection;



/**
 * MySQL facade.
 */
public final class MysqlFacade extends BaseFacade {

  public MysqlFacade(@NotNull Rdbms rdbms,
                     @NotNull DataSource source,
                     @NotNull DBErrorRecognizer recognizer, @NotNull SQL sql) {
    super(rdbms, source, recognizer, sql);
  }


  @NotNull
  @Override
  protected MysqlSession createFacadeForConnection(@NotNull Connection connection) {
    return new MysqlSession(this, connection, true);
  }

}
