package org.jetbrains.jdba.core1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.DBCommandRunner;
import org.jetbrains.jdba.core.DBQueryRunner;
import org.jetbrains.jdba.core.DBScriptRunner;
import org.jetbrains.jdba.sql.SqlCommand;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.sql.SqlScript;



/**
 *
 **/
public interface DBTransaction {

  public DBCommandRunner command(@NotNull final SqlCommand command);

  public DBCommandRunner command(@NotNull final String commandText);

  public <S> DBQueryRunner<S> query(@NotNull final SqlQuery<S> query);

  public DBScriptRunner script(@NotNull final SqlScript script);

}
