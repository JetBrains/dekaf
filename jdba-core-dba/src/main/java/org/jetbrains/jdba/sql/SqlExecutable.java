package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class SqlExecutable {

  /**
   * Number of lines skipped from source text.
   * Zero if nothing skipped.
   */
  final int myRow;

  /**
   * Query source text.
   */
  @NotNull
  final String mySourceText;


  protected SqlExecutable(@NotNull TextFragment sourceFragment) {
    this.mySourceText = sourceFragment.text;
    this.myRow = sourceFragment.row;
  }


  protected SqlExecutable(@NotNull String sourceText) {
    this.mySourceText = sourceText;
    this.myRow = 1;
  }


  public SqlExecutable(@NotNull final String sourceText, final int row) {
    this.mySourceText = sourceText;
    this.myRow = row;
  }

  public int getRow() {
    return myRow;
  }

  @NotNull
  public String getSourceText() {
    return mySourceText;
  }
}
