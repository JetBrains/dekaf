package org.jetbrains.jdba.rdbms.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.Rdbms;
import org.jetbrains.jdba.core.BaseFacade;
import org.jetbrains.jdba.core.DBErrorRecognizer;
import org.jetbrains.jdba.sql.SQL;

import javax.sql.DataSource;
import java.sql.Connection;



/**
 * Oracle DBMS facade.
 */
public final class OraFacade extends BaseFacade {

  public OraFacade(@NotNull Rdbms rdbms,
                   @NotNull DataSource source,
                   @NotNull DBErrorRecognizer recognizer, @NotNull SQL sql) {
    super(rdbms, source, recognizer, sql);
  }


  @NotNull
  @Override
  protected OraSession createSessionForConnection(@NotNull Connection connection) {
    return new OraSession(this, connection, true);
  }

}
