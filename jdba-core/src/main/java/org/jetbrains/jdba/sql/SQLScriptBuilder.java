package org.jetbrains.jdba.sql;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jetbrains.jdba.utils.Strings.rtrim;



/**
 * A builder for SQL script.
 * @author Leonid Bushuev from JetBrains
 */
public class SQLScriptBuilder {

  private final ImmutableList.Builder<SQLCommand> myCommands =
    new ImmutableList.Builder<SQLCommand>();



  public void add(@NotNull String... commands) {
    for (String command : commands) {
      SQLCommand cmd = new SQLCommand(command);
      myCommands.add(cmd);
    }
  }

  public void add(@NotNull SQLCommand... commands) {
    for (SQLCommand command : commands) {
      myCommands.add(command);
    }
  }

  public void add(@NotNull SQLScript... scripts) {
    for (SQLScript script : scripts) {
      for (SQLCommand command : script.myCommands) {
        myCommands.add(command);
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
    int rowOffset = begin.offset;
    while (!walker.isEOT()) {
      rowOffset = walker.getOffset();
      String row = walker.popRow().trim();
      if (row.equals("/")) break;
    }

    String plText = rtrim(walker.getText().substring(begin.offset, rowOffset));
    SQLCommand command = new SQLCommand(begin.row - 1, plText);
    myCommands.add(command);
  }


  private static final Pattern SQL_END_MARKER =
    Pattern.compile("(;|\\n\\s*/)(\\s|\\n|--[^\\n]*?\\n|/\\*.*?\\*/)*?(\\n|$)|$",
                    Pattern.DOTALL);

  private void extractSQLCommand(@NotNull TextWalker walker) {
    skipEmptySpace(walker);
    final TextPointer begin = walker.getPointer();
    final Matcher matcher = walker.skipToPattern(SQL_END_MARKER);

    final String sqlText = rtrim(walker.getText().substring(begin.offset, walker.getOffset()));
    SQLCommand command = new SQLCommand(begin.row - 1, sqlText);
    myCommands.add(command);

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
  public SQLScript build() {
    ImmutableList<SQLCommand> commands = myCommands.build();
    return commands.size() > 0 ? new SQLScript(commands) : SQL.EMPTY_SCRIPT;
  }


}
