package org.jetbrains.dba.base;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;



/**
 * One SQL script.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class Script {

  @NotNull
  final ImmutableList<Command> commands;


  public Script(@NotNull final List<Command> commands) {
    this(ImmutableList.copyOf(commands));
  }


  Script(@NotNull final ImmutableList<Command> commands) {
    this.commands = commands;
  }


  @NotNull
  public List<Command> getCommands() {
    return commands;
  }
}
