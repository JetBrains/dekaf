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
  final int myLineOffset;

  /**
   * Query source text.
   */
  @NotNull
  final String mySourceText;


  protected SqlExecutable(@NotNull String sourceText) {
    this.mySourceText = sourceText;
    this.myLineOffset = 0;
  }


  public SqlExecutable(@NotNull final String sourceText, final int lineOffset) {
    this.mySourceText = sourceText;
    this.myLineOffset = lineOffset;
  }


  public int getLineOffset() {
    return myLineOffset;
  }


  @NotNull
  public String getSourceText() {
    return mySourceText;
  }
}
