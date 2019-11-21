package org.jetbrains.dekaf.sql;

/**
 * @author Leonid Bushuev from JetBrains
 */
final class TextPointer {

  /**
   * Offset from the beginning of the text.
   */
  final int offset;

  /**
   * Row number, starts with 1.
   */
  final int row;

  /**
   * Number of positions inside the row, starts with 1.
   */
  final int pos;


  TextPointer(int offset, int row, int pos) {
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
