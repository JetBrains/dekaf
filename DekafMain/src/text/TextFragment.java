package org.jetbrains.dekaf.text;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class TextFragment implements Comparable<TextFragment>, Serializable {

  @NotNull
  public final String text;

  /**
   * Row number, starts with 1.
   */
  public final int row;

  /**
   * Number of the position inside the row, starts with 1.
   */
  public final int pos;


  /**
   * Instantiates a new TextFragment.
   * @param text  text fragment itself.
   * @param row   the row number, starts with 1.
   * @param pos   the number of the position inside the row, starts with 1.
   */
  public TextFragment(@NotNull final String text, final int row, final int pos) {
    this.text = text;
    this.row = row;
    this.pos = pos;
  }


  @NotNull
  public String getTextName() {
    return "SQL";
  }


  @Override
  public int compareTo(@NotNull final TextFragment that) {
    if (this == that) return 0;
    //noinspection ConstantConditions
    if (that == null) throw new IllegalArgumentException("Comparison with null");

    int z =         this.getTextName().compareTo(that.getTextName());
    if (z == 0) z = this.row - that.row;
    if (z == 0) z = this.pos - that.pos;
    if (z == 0) z = this.text.compareTo(that.text);
    return z;
  }


  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TextFragment that = (TextFragment) o;

    return this.row == that.row
        && this.pos == that.pos
        && this.text.equals(that.text);
  }

  @Override
  public int hashCode() {
    return text.hashCode();
  }


  @NotNull
  protected String getTextLocationHumanReadable() {
    return "Fragment at " + row + ':' + pos;
  }


  @Override
  public String toString() {
    return getTextLocationHumanReadable() + '\n'
        + text;
  }

}
