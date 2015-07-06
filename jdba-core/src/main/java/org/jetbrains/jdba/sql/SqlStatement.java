package org.jetbrains.jdba.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



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
   * The natural name of the statement.
   * Used, for example, when the statement was got from the {@link Scriptum}.
   */
  @Nullable
  final String myName;

  /**
   * A short (one line) description of this SQL statement.
   * It may contain a file, position and optional name of this SQL statement.
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
      myName = name;
      if (name == null) name = "SQL fragment";
      mainDescriptionPart = name + " from " + f.getTextName() + ':' + f.row + ':' + f.pos;
    }
    else {
      //noinspection UnnecessaryLocalVariable
      TextFragment f = sourceFragment;
      mainDescriptionPart = "SQL statement from " + f.getTextName() + ':' + f.row + ':' + f.pos;
      myName = null;
    }

    // TODO add some additional info to the mainDescriptionPart
    myDescription = mainDescriptionPart;
  }


  protected SqlStatement(@NotNull String sourceText) {
    mySourceText = sourceText;
    myRow = 1;
    myName = null;
    myDescription = "SQL statement";
  }


  @Deprecated
  protected SqlStatement(@NotNull final String sourceText, final int row) {
    this.mySourceText = sourceText;
    this.myRow = row;
    this.myName = '@' + Integer.toString(row);
    this.myDescription = "SQL statement at row " + row;
  }

  public int getRow() {
    return myRow;
  }

  @NotNull
  public String getSourceText() {
    return mySourceText;
  }

  @Nullable
  protected String getName() {
    return myName;
  }

  @NotNull
  protected String getDescription() {
    return myDescription;
  }


  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    if (myName != null) b.append(myName).append(": ");
    b.append(myDescription).append(":\n");
    b.append(mySourceText);
    return b.toString();
  }
}
