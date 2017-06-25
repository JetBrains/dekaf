package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.dekaf.text.Scriptum;
import org.jetbrains.dekaf.text.TextFileFragment;
import org.jetbrains.dekaf.text.TextFragment;

import java.util.function.Function;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class SqlStatement {

  //// STATE \\\\

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


  //// CONSTRUCTORS \\\\

  protected SqlStatement(@NotNull TextFragment sourceFragment) {
    mySourceText = sourceFragment.getText();
    myRow = sourceFragment.getRow();

    final String mainDescriptionPart;
    if (sourceFragment instanceof TextFileFragment) {
      TextFileFragment f = (TextFileFragment) sourceFragment;
      String name = f.getFragmentName();
      myName = name;
      if (name == null) name = "SQL fragment";
      mainDescriptionPart = name + " from " + f.getTextName() + ':' + f.getRow() + ':' + f.getPos();
    }
    else {
      //noinspection UnnecessaryLocalVariable
      TextFragment f = sourceFragment;
      mainDescriptionPart = "SQL statement from " + f.getTextName() + ':' + f.getRow() + ':' + f.getPos();
      myName = null;
    }

    // TODO add some additional info to the mainDescriptionPart
    myDescription = mainDescriptionPart;
  }


  protected SqlStatement(@NotNull String sourceText) {
    this(sourceText, 1, null);
  }


  protected SqlStatement(@NotNull final String sourceText,
                         final int row,
                         @Nullable final String statementName) {
    this(
        row,
        sourceText,
        statementName != null ? statementName : '@' + Integer.toString(row),
        statementName != null ? statementName : "SQL statement at row " + row
    );
  }

  /**
   * Trivial constructor.
   * @param row           row number (starting from 1)
   * @param sourceText
   * @param name
   * @param description
   */
  protected SqlStatement(final int row,
                         @NotNull final String sourceText,
                         @Nullable final String name,
                         @NotNull final String description) {
    myRow = row;
    mySourceText = sourceText;
    myName = name;
    myDescription = description;
  }


  //// MANIPULATION AND MUTATION \\\\

  @NotNull
  public abstract SqlStatement rewrite(@NotNull final Function<String,String> operator);



  //// GETTERS AND FUNCTIONS (that don't modify the structure) \\\\

  public int getRow() {
    return myRow;
  }

  @NotNull
  public String getSourceText() {
    return mySourceText;
  }

  @Nullable
  public String getName() {
    return myName;
  }

  @NotNull
  public String getDescription() {
    return myDescription;
  }


  //// LEGACY FUNCTIONS \\\\

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    if (myName != null) b.append(myName).append(": ");
    b.append(myDescription).append(":\n");
    b.append(mySourceText);
    return b.toString();
  }
}
