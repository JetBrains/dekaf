package org.jetbrains.jdba.postgre;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core1.BaseFacade;
import org.jetbrains.jdba.intermediate.DBErrorRecognizer;
import org.jetbrains.jdba.sql.SQL;

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
  protected PostgreSession createSessionForConnection(@NotNull Connection connection) {
    return new PostgreSession(this, connection, true);
  }

}
