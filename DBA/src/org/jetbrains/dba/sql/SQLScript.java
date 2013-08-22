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

  public SQLScript(@NotNull final SQLScript... scripts) {
    this(joinCommands(scripts));
  }

  private static ImmutableList<SQLCommand> joinCommands(SQLScript[] scripts) {
    ImmutableList.Builder<SQLCommand> b = ImmutableList.builder();
    for (SQLScript script : scripts) {
      b.addAll(script.getCommands());
    }
    return b.build();
  }


  SQLScript(@NotNull final ImmutableList<SQLCommand> commands) {
    this.commands = commands;
  }


  @NotNull
  public List<SQLCommand> getCommands() {
    return commands;
  }


  @Override
  @NotNull
  public String toString() {
    int n = commands.size();
    switch (n) {
      case 0: return "";
      case 1: return commands.get(0).getSourceText();
      default:
        final StringBuilder b = new StringBuilder();
        final String delimiterString = getScriptDelimiterString();
        b.append(commands.get(0).getSourceText());
        for (int i = 1; i < n; i++) {
          if (b.charAt(b.length()-1) != '\n')  b.append('\n');
          b.append(delimiterString).append('\n');
          b.append(commands.get(i).getSourceText());
        }
        return b.toString();
    }
    // TODO cache it
  }


  @NotNull
  protected String getScriptDelimiterString() {
    return ";";
  }
}
