package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;

import java.util.*;



/**
 * One SQL script.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SqlScript {

  @NotNull
  private final SqlStatement[] myStatements;

  private final int myCount;



  public SqlScript(@NotNull final String... statements) {
    this(makeStatementsFromStrings(statements));
  }

  @NotNull
  private static SqlStatement[] makeStatementsFromStrings(final @NotNull String[] statements) {
    int n = statements.length;
    SqlStatement[] r = new SqlStatement[n];
    for (int i = 0; i < n; i++) r[i] = new SqlCommand(statements[i]);
    return r;
  }

  public SqlScript(@NotNull final SqlStatement... statements) {
    this(statements, true);
  }

  public SqlScript(@NotNull final SqlScript... scripts) {
    this(joinStatements(scripts));
  }

  private static List<SqlStatement> joinStatements(SqlScript[] scripts) {
    List<SqlStatement> b = new ArrayList<SqlStatement>();
    for (SqlScript script : scripts) {
      b.addAll(script.getStatements());
    }
    return b;
  }

  public SqlScript(@NotNull final Collection<? extends SqlStatement> statements) {
    this(statements.toArray(new SqlStatement[statements.size()]), false);
  }

  private SqlScript(@NotNull final SqlStatement[] statements, boolean copy) {
    final int n = statements.length;
    if (copy) {
      this.myStatements = new SqlStatement[n];
      System.arraycopy(statements, 0, myStatements, 0, n);
    }
    else {
      this.myStatements = statements;
    }
    this.myCount = n;
  }


  @NotNull
  public List<? extends SqlStatement> getStatements() {
    return Collections.unmodifiableList(Arrays.asList(myStatements));
  }


  public boolean hasStatements() {
    return myCount > 0;
  }

  public int count() {
    return myCount;
  }

  @Override
  @NotNull
  public String toString() {
    switch (myCount) {
      case 0: return "";
      case 1: return myStatements[0].getSourceText();
      default:
        final StringBuilder b = new StringBuilder();
        final String delimiterString = getScriptDelimiterString();
        b.append(myStatements[0].getSourceText());
        for (int i = 1; i < myCount; i++) {
          if (b.charAt(b.length()-1) != '\n')  b.append('\n');
          b.append(delimiterString).append('\n');
          b.append(myStatements[i].getSourceText());
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
