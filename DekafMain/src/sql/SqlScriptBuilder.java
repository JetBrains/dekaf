package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.text.TextPointer;
import org.jetbrains.dekaf.text.TextWalker;
import org.jetbrains.dekaf.util.StringExt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * A builder for SQL script.
 * @author Leonid Bushuev from JetBrains
 */
public class SqlScriptBuilder {

  private final ArrayList<SqlStatement> myStatements =
    new ArrayList<SqlStatement>();



  public void add(@NotNull String... commands) {
    for (String command : commands) {
      SqlCommand cmd = new SqlCommand(command);
      myStatements.add(cmd);
    }
  }

  public void add(@NotNull SqlCommand... commands) {
    Collections.addAll(myStatements, commands);
  }

  public void add(@NotNull SqlScript... scripts) {
    for (SqlScript script : scripts) {
      if (script.hasStatements()) {
        for (SqlStatement statement : script.getStatements()) {
          myStatements.add(statement);
        }
      }
    }
  }



  public void parse(@NotNull final String text) {
    TextWalker walker = new TextWalker(text);
    while (!walker.isEOT()) {
      skipEmptySpace(walker);
      if (walker.isEOT()) break;

      String essentialWords = extractEssentialWords(walker, 6);
      boolean pl = determinePL(essentialWords);
      if (pl) {
        extractPLBlock(walker);
      }
      else {
        extractSQLCommand(walker);
      }
    }
  }


  @SuppressWarnings("ConstantConditions")
  static String extractEssentialWords(@NotNull final TextWalker walker, int limitWords) {
    TextWalker w = walker.clone();
    StringBuilder b = new StringBuilder(40);
    int wordsCnt = 0;
    boolean inWord = false;
    boolean inSingleLineComment = false;
    boolean inMultiLineComment = false;
    while (!w.isEOT()) {
      char c = w.getChar();
      char c2 = w.getNextChar();
      final boolean isWordChar = Character.isJavaIdentifierPart(c);
      if (inSingleLineComment) {
        if (c == '\n') inSingleLineComment = false;
      }
      else if (inMultiLineComment) {
        if (c == '*' && c2 == '/') {
          w.next(); // additional w.next() - because 2 chars
          inMultiLineComment = false;
        }
      }
      else if (inWord && !isWordChar) {
        wordsCnt++;
        if (wordsCnt >= limitWords) break;
        b.append(' ');
        inWord = false;
      }
      else if (isWordChar) {
        b.append(Character.toLowerCase(c));
        inWord = true;
      }
      else if (!isWordChar) {
        if (c == '-' && c2 == '-') {
          inSingleLineComment = true;
          w.next();
        }
        if (c == '/' && c2 == '*') {
          inMultiLineComment = true;
          w.next();
        }
      }
      w.next();
    }
    return b.toString().trim();
  }


  private void extractPLBlock(@NotNull TextWalker walker) {
    final TextPointer begin = walker.getPointer();
    int position;
    while (true) {
      position = walker.getOffset();
      if (walker.isEOT()) break;
      String row = walker.popRow().trim();
      if (row.equals("/")) break;
    }

      String plText = StringExt.rtrim(walker.getText().substring(begin.offset, position));
    SqlCommand command = new SqlCommand(plText, begin.row, null);
    myStatements.add(command);
  }


  private static final Pattern SQL_END_MARKER =
    Pattern.compile("(;|\\n\\s*/)(\\s|\\n|--[^\\n]*?\\n|/\\*.*?\\*/)*?(\\n|$)|$",
                    Pattern.DOTALL);

  private void extractSQLCommand(@NotNull TextWalker walker) {
    skipEmptySpace(walker);
    final TextPointer begin = walker.getPointer();
    final Matcher matcher = walker.skipToPattern(SQL_END_MARKER);

      final String sqlText = StringExt.rtrim(walker.getText()
                                                   .substring(begin.offset, walker.getOffset()));
    SqlCommand command = new SqlCommand(sqlText, begin.row, null);
    myStatements.add(command);

    if (matcher != null) {
      walker.skipToOffset(matcher.end());
    }
  }


  private static final Pattern PL_ESSENTIAL_WORDS_PATTERN =
    Pattern.compile("^(declare|begin|(create (or replace )?(type|package|procedure|function|trigger))).*");


  boolean determinePL(@NotNull final String essentialWords) {
    return PL_ESSENTIAL_WORDS_PATTERN.matcher(essentialWords).matches();
  }


  protected void skipEmptySpace(@NotNull TextWalker walker) {
    while (!walker.isEOT()) {
      char c1 = walker.getChar();
      if (Character.isWhitespace(c1)) {
        walker.next();
        continue;
      }
      char c2 = walker.getNextChar();
      if (c1 == '-' && c2 == '-') {
        while (!walker.isEOT()) {
          walker.next();
          if (walker.getChar() == '\n') {
            walker.next();
            break;
          }
        }
      }
      if (c1 == '/' && c2 == '*') {
        walker.next();
        walker.next();
        while (!walker.isEOT()) {
          if (walker.getChar() == '*' && walker.getNextChar() == '/') {
            walker.next();
            walker.next();
            break;
          }
          walker.next();
        }
      }
      break;
    }
  }


  @NotNull
  public SqlScript build() {
    return new SqlScript(myStatements);
  }


}
