package org.jetbrains.dba.sql;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class SQLExecutable {

  final int myLineOffset;

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
