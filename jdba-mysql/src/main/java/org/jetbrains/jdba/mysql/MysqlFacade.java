package org.jetbrains.jdba.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core1.BaseFacade;
import org.jetbrains.jdba.intermediate.DBErrorRecognizer;
import org.jetbrains.jdba.sql.SQL;

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
  protected MysqlSession createSessionForConnection(@NotNull Connection connection) {
    return new MysqlSession(this, connection, true);
  }

}
