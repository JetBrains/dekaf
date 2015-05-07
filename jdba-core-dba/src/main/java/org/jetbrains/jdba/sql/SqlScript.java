package org.jetbrains.jdba.sql;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;



/**
 * One SQL script.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SqlScript {

  @NotNull
  final ImmutableList<SqlCommand> myCommands;

  final int myCount;



  public SqlScript(@NotNull final String... commands) {
    this(makeCommandsFromStrings(commands));
  }

  @NotNull
  private static ImmutableList<SqlCommand> makeCommandsFromStrings(final @NotNull String[] commands) {
    ImmutableList.Builder<SqlCommand> builder = ImmutableList.builder();
    for (String command : commands) {
      SqlCommand cmd = new SqlCommand(command);
      builder.add(cmd);
    }
    return builder.build();
  }

  public SqlScript(@NotNull final SqlCommand... commands) {
    this(ImmutableList.copyOf(commands));
  }

  public SqlScript(@NotNull final List<SqlCommand> commands) {
    this(ImmutableList.copyOf(commands));
  }

  public SqlScript(@NotNull final SqlScript... scripts) {
    this(joinCommands(scripts));
  }

  private static ImmutableList<SqlCommand> joinCommands(SqlScript[] scripts) {
    ImmutableList.Builder<SqlCommand> b = ImmutableList.builder();
    for (SqlScript script : scripts) {
      b.addAll(script.getCommands());
    }
    return b.build();
  }


  protected SqlScript(@NotNull final ImmutableList<SqlCommand> commands) {
    this.myCommands = commands;
    this.myCount = commands.size();
  }


  @NotNull
  public List<SqlCommand> getCommands() {
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
