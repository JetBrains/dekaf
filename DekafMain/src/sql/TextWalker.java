package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Text with a position pointer. Also counts line numbers.
 * @author Leonid Bushuev from JetBrains
 */
final class TextWalker implements Cloneable {

  @NotNull
  private final String myText;

  private final int myLength;

  /**
   * Offset from the beginning of the text.
   */
  private int myOffset = 0;

  /**
   * Row number, starts with 1.
   */
  private int myRow = 1;

  /**
   * Number of positions inside the row, starts with 1.
   */
  private int myPos = 1;

  /**
   * Current character.
   */
  private char myChar;



  TextWalker(@NotNull final String text) {
    myText = text;
    myLength = myText.length();
    myChar = myLength > 0 ? myText.charAt(0) : '\0';
  }


  @NotNull
  public TextPointer getPointer() {
    return new TextPointer(myOffset, myRow, myPos);
  }


  public void setPointer(@NotNull final TextPointer pointer) {
    myOffset = pointer.offset;
    myRow = pointer.row;
    myPos = pointer.pos;
    myChar = myLength > 0 ? myText.charAt(0) : '\0';
  }


  public void next() {
    if (myOffset >= myLength) throw new IllegalStateException("End of Text");

    if (myChar == '\n') {
      myRow++;
      myPos = 1;
    }
    else {
      myPos++;
    }

    myOffset++;
    myChar = myOffset < myLength ? myText.charAt(myOffset) : '\0';
  }


  public String popRow() {
    int offset1 = myOffset,
        row1 = myRow;
    while (row1 == myRow && !isEOT()) next();
    return myText.substring(offset1, myOffset);
  }


  @Nullable
  public Matcher skipToPattern(@NotNull final Pattern pattern) {
    final Matcher m = pattern.matcher(myText);
    final boolean found = m.find(myOffset);
    if (found) {
      int goal = m.start();
      while (myOffset < goal) next();
      return m;
    }
    else {
      while (myOffset < myLength) next();
      return null;
    }
  }


  public void skipToOffset(int newOffset) {
    if (newOffset < myOffset) throw new IllegalArgumentException("Attempted to skip back");
    if (newOffset > myLength) throw new IllegalArgumentException("Attempted to skip out of the end of text");

    int distance = newOffset - myOffset;
    for (int i = 1; i <= distance; i++) next();
  }


  @NotNull
  public String getText() {
    return myText;
  }


  public int getLength() {
    return myLength;
  }


  public int getOffset() {
    return myOffset;
  }


  public int getRow() {
    return myRow;
  }


  public int getPos() {
    return myPos;
  }


  public char getChar() {
    return myChar;
  }

  public char getNextChar() {
    int k = myOffset + 1;
    return k < myLength ? myText.charAt(k) : '\0';
  }


  public boolean isEOT() {
    return myOffset >= myLength;
  }


  @SuppressWarnings("CloneDoesntCallSuperClone")
  public TextWalker clone() {
    TextWalker clone = new TextWalker(myText);
    clone.myOffset = myOffset;
    clone.myRow = myRow;
    clone.myPos = myPos;
    clone.myChar = myChar;
    return clone;
  }


}
