package org.jetbrains.dba.sql;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;



/**
 * One SQL script.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SQLScript {

  @NotNull
  final ImmutableList<SQLCommand> commands;


  public SQLScript(@NotNull final SQLCommand... commands) {
    this(ImmutableList.copyOf(commands));
  }

  public SQLScript(@NotNull final List<SQLCommand> commands) {
    this(ImmutableList.copyOf(commands));
  }


  SQLScript(@NotNull final ImmutableList<SQLCommand> commands) {
    this.commands = commands;
  }


  @NotNull
  public List<SQLCommand> getCommands() {
    return commands;
  }
}
