package org.jetbrains.dba.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.sql.SQLCommand;
import org.jetbrains.dba.sql.SQLQuery;



/**
 *
 **/
public interface DBTransaction {

  public DBCommandRunner command(@NotNull final SQLCommand command);

  public DBCommandRunner command(@NotNull final String commandText);

  public <S> DBQueryRunner<S> query(@NotNull final SQLQuery<S> query);
}
