package org.jetbrains.jdba.sql;

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
  final ImmutableList<SQLCommand> myCommands;

  final int myCount;


  @Deprecated
  public SQLScript(@NotNull final SQLCommand... commands) {
    this(ImmutableList.copyOf(commands));
  }

  @Deprecated
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


  protected SQLScript(@NotNull final ImmutableList<SQLCommand> commands) {
    this.myCommands = commands;
    this.myCount = commands.size();
  }


  @NotNull
  public List<SQLCommand> getCommands() {
    return myCommands;
  }


  public boolean hasCommands() {
    return myCount > 0;
  }


  @Override
  @NotNull
  public String toString() {
    switch (myCount) {
      case 0: return "";
      case 1: return myCommands.get(0).getSourceText();
      default:
        final StringBuilder b = new StringBuilder();
        final String delimiterString = getScriptDelimiterString();
        b.append(myCommands.get(0).getSourceText());
        for (int i = 1; i < myCount; i++) {
          if (b.charAt(b.length()-1) != '\n')  b.append('\n');
          b.append(delimiterString).append('\n');
          b.append(myCommands.get(i).getSourceText());
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
