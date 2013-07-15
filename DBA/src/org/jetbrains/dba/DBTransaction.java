package org.jetbrains.dba;

import org.jetbrains.annotations.NotNull;



/**
 *
 **/
public interface DBTransaction {

  public CommandRunner command(@NotNull final Command command);

  public CommandRunner command(@NotNull final String commandText);

  public <S> QueryRunner<S> query(@NotNull final Query<S> query);
}
