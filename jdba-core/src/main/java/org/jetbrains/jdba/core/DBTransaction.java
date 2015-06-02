package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.sql.SqlCommand;
import org.jetbrains.jdba.sql.SqlQuery;
import org.jetbrains.jdba.sql.SqlScript;



/**
 * Database transaction.
 **/
public interface DBTransaction {

  @NotNull
  DBCommandRunner command(@NotNull final SqlCommand command);

  @NotNull
  DBCommandRunner command(@NotNull final String commandText);

  @NotNull
  <S> DBQueryRunner<S> query(@NotNull final SqlQuery<S> query);

  @NotNull
  <S> DBQueryRunner<S> query(@NotNull final String queryText,
                             @NotNull final ResultLayout<S> layout);

  @NotNull
  DBScriptRunner script(@NotNull final SqlScript script);

}
