package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class SqlStatement {

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

  /**
   * A short (one line) description of this SQL statement.
   * It contains a file, position and optional name of this SQL statement.
   */
  @NotNull
  final String myDescription;



  protected SqlStatement(@NotNull TextFragment sourceFragment) {
    mySourceText = sourceFragment.text;
    myRow = sourceFragment.row;

    final String mainDescriptionPart;
    if (sourceFragment instanceof TextFileFragment) {
      TextFileFragment f = (TextFileFragment) sourceFragment;
      String name = f.getFragmentName();
      if (name == null) name = "SQL fragment";
      mainDescriptionPart = name + " from " + f.getTextName() + ':' + f.row + ':' + f.pos;
    }
    else {
      //noinspection UnnecessaryLocalVariable
      TextFragment f = sourceFragment;
      mainDescriptionPart = "SQL statement from " + f.getTextName() + ':' + f.row + ':' + f.pos;
    }

    // TODO add some additional info to the mainDescriptionPart
    myDescription = mainDescriptionPart;
  }


  protected SqlStatement(@NotNull String sourceText) {
    mySourceText = sourceText;
    myRow = 1;
    myDescription = "SQL statement";
  }


  @Deprecated
  protected SqlStatement(@NotNull final String sourceText, final int row) {
    this.mySourceText = sourceText;
    this.myRow = row;
    this.myDescription = "SQL statement at row " + row;
  }

  public int getRow() {
    return myRow;
  }

  @NotNull
  public String getSourceText() {
    return mySourceText;
  }

  @NotNull
  protected String getDescription() {
    return myDescription;
  }


  @Override
  public String toString() {
    return myDescription + ":\n" + mySourceText;
  }
}
