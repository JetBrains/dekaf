package org.jetbrains.dba.sql;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class SQLExecutable {

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


  protected SQLExecutable(@NotNull String sourceText) {
    this.mySourceText = sourceText;
    this.myLineOffset = 0;
  }


  public SQLExecutable(@NotNull final String sourceText, final int lineOffset) {
    this.mySourceText = sourceText;
    this.myLineOffset = lineOffset;
  }


  @NotNull
  public String getSourceText() {
    return mySourceText;
  }
}
