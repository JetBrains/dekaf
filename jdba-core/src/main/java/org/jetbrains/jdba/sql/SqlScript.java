package org.jetbrains.jdba.sql;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;



/**
 * One SQL script.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SqlScript {

  @NotNull
  final ImmutableList<? extends SqlStatement> myStatements;

  final int myCount;



  public SqlScript(@NotNull final String... statements) {
    this(makeStatementsFromStrings(statements));
  }

  @NotNull
  private static ImmutableList<? extends SqlStatement> makeStatementsFromStrings(final @NotNull String[] statements) {
    ImmutableList.Builder<SqlCommand> builder = ImmutableList.builder();
    for (String command : statements) {
      SqlCommand cmd = new SqlCommand(command);
      builder.add(cmd);
    }
    return builder.build();
  }

  public SqlScript(@NotNull final SqlStatement... statements) {
    this(ImmutableList.copyOf(statements));
  }

  public SqlScript(@NotNull final Collection<? extends SqlStatement> statements) {
    this(ImmutableList.copyOf(statements));
  }

  public SqlScript(@NotNull final SqlScript... scripts) {
    this(joinStatements(scripts));
  }

  private static ImmutableList<? extends SqlStatement> joinStatements(SqlScript[] scripts) {
    ImmutableList.Builder<SqlStatement> b = ImmutableList.builder();
    for (SqlScript script : scripts) {
      b.addAll(script.getStatements());
    }
    return b.build();
  }


  protected SqlScript(@NotNull final ImmutableList<? extends SqlStatement> statements) {
    this.myStatements = statements;
    this.myCount = statements.size();
  }


  @NotNull
  public List<? extends SqlStatement> getStatements() {
    return myStatements;
  }


  public boolean hasStatements() {
    return myCount > 0;
  }


  @Override
  @NotNull
  public String toString() {
    switch (myCount) {
      case 0: return "";
      case 1: return myStatements.get(0).getSourceText();
      default:
        final StringBuilder b = new StringBuilder();
        final String delimiterString = getScriptDelimiterString();
        b.append(myStatements.get(0).getSourceText());
        for (int i = 1; i < myCount; i++) {
          if (b.charAt(b.length()-1) != '\n')  b.append('\n');
          b.append(delimiterString).append('\n');
          b.append(myStatements.get(i).getSourceText());
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
