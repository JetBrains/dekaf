package org.jetbrains.dekaf.text;

/**
 * @author Leonid Bushuev from JetBrains
 */
public final class TextPointer {

  /**
   * Offset from the beginning of the text.
   */
  public final int offset;

  /**
   * Row number, starts with 1.
   */
  public final int row;

  /**
   * Number of positions inside the row, starts with 1.
   */
  public final int pos;


  public TextPointer(int offset, int row, int pos) {
    this.offset = offset;
    this.row = row;
    this.pos = pos;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TextPointer pointer = (TextPointer) o;

    return offset == pointer.offset && pos == pointer.pos && row == pointer.row;
  }


  @Override
  public int hashCode() {
    return offset;
  }
}
