package org.jetbrains.dba.sql;

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

    TextPointer pointer = (TextPointer)o;

    if (offset != pointer.offset) return false;
    if (pos != pointer.pos) return false;
    if (row != pointer.row) return false;

    return true;
  }


  @Override
  public int hashCode() {
    return offset;
  }
}
