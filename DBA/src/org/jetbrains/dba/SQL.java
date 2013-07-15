package org.jetbrains.dba;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.dba.errors.DBPreparingError;
import org.jetbrains.dba.utils.IntRef;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jetbrains.dba.utils.Strings.rtrim;



/**
 * SQL queries and commands factory.
 *
 * @author Leonid Bushuev from JetBrains
 */
public class SQL {

  private Map<String, String> sources =
    new ConcurrentHashMap<String, String>();


  public SQL() {
  }


  public SQL(final String... resourcePath) {
    if (resourcePath != null) {
      for (String rp : resourcePath) {
        loadSourcesFromResource(rp);
      }
    }
  }


  @NotNull
  public Script loadScript(@NotNull final File scriptFile, Charset charset)
    throws IOException {
    if (!scriptFile.isFile()) {
      throw new IllegalArgumentException("The path '" + scriptFile + "' is not a file.");
    }

    final List<String> scriptLines = Files.readLines(scriptFile, charset);

    return loadScript(scriptLines);
  }


  public Script loadScript(@NotNull final List<String> scriptLines) {
    final ImmutableList.Builder<Command> b = ImmutableList.<Command>builder();


    IntRef curr = new IntRef(findCommandBegin(scriptLines, 0));
    while (curr.value >= 0) {
      Command cmd = loadCommand(scriptLines, curr);
      b.add(cmd);
      curr.value = findCommandBegin(scriptLines, curr.value);
    }

    return new Script(b.build());
  }


  protected int findCommandBegin(@NotNull final List<String> scriptLines, final int from) {
    for (int k = from, n = scriptLines.size(); k < n; k++) {
      String line = scriptLines.get(k).trim();
      if (line.length() == 0) continue;
      if (line.startsWith("--") && !line.startsWith("--+")) continue;
      // TODO handle multiline comments
      return k;
    }

    return -1;
  }


  private static final Pattern ORA_SQLPLUS_NEST_FILE =
    Pattern.compile("^\\s*@.*$");

  private static final Pattern ORA_PLSQL_BEGIN_PATTERN =
    Pattern.compile("^\\s*(/\\*.*\\*/\\s*)*(declare|begin).*$",
                    Pattern.CASE_INSENSITIVE);

  private static final Pattern ORA_PLSQL_END_PATTERN =
    Pattern.compile("^\\s*/\\s*(/\\*.*\\*/\\s*)*(--.*)?$",
                    Pattern.CASE_INSENSITIVE);

  private static final Pattern ORA_SQL_END_PATTERN =
    Pattern.compile("^.*(;)\\s*(/\\*.*\\*/\\s*)*(--.*)?$",
                    Pattern.CASE_INSENSITIVE);


  @NotNull
  protected Command loadCommand(@NotNull final List<String> scriptLines, @NotNull final IntRef currLineNr) {
    int firstLineNr = currLineNr.value;

    final String firstLine = scriptLines.get(firstLineNr);
    if (ORA_SQLPLUS_NEST_FILE.matcher(firstLine).matches()) {
      currLineNr.value++;
      return instantiateCommand(firstLineNr, firstLine.trim());
    }

    StringBuilder buf = new StringBuilder();
    int n = scriptLines.size();
    if (ORA_PLSQL_BEGIN_PATTERN.matcher(firstLine).matches()) {
      // it is an Oracle PL/SQL script.
      // the only possible delimiter is a slash on the a separated line
      int endNr = n;
      for (int k = firstLineNr + 1; k < n; k++) {
        String line = scriptLines.get(k);
        if (ORA_PLSQL_END_PATTERN.matcher(line).matches()) {
          endNr = k;
          break;
        }
      }
      for (int i = firstLineNr; i < n; i++) buf.append(scriptLines.get(i));
      currLineNr.value = endNr + 1;
    }
    else {
      // a singe SQL statement
      // the delimiter can be both - semicolon or a single slash
      currLineNr.value = n; // we'll rewrite it when an end marker found
      for (int k = firstLineNr; k < n; k++) {
        String line = scriptLines.get(k);
        Matcher m;
        if (ORA_PLSQL_END_PATTERN.matcher(line).matches()) {
          currLineNr.value = k + 1;
          break;
        }
        else if ((m = ORA_SQL_END_PATTERN.matcher(line)).matches()) {
          int semicolon = m.start(1);
          String part = rtrim(line.substring(0, semicolon));
          buf.append(part);
          currLineNr.value = k + 1;
          break;
        }
        else {
          buf.append(rtrim(line));
        }
      }
    }

    preprocessCommandText(buf);
    return instantiateCommand(firstLineNr, buf.toString());
  }


  protected void preprocessCommandText(@NotNull final StringBuilder buf) {
  }


  @NotNull
  protected Command instantiateCommand(final int begin, @NotNull final String sourceText) {
    return new Command(begin, sourceText);
  }


  protected static void removeTailSemicolon(@NotNull final StringBuilder b) {
    int n = b.length();
    if (n == 0) return;
    if (b.charAt(n - 1) == ';') b.delete(n - 1, n);
  }


  public void loadSourcesFromResource(@NotNull final String resourcePath) {
    final Map<String, String> theSources = SqlUtil.loadResourceStatements(resourcePath);
    sources.putAll(theSources);
  }


  @NotNull
  public Command command(@NotNull final String sourceText) {
    String text = preprocess(sourceText);
    return new Command(text);
  }


  @NotNull
  public <S> Query<S> query(@NotNull final String sourceText,
                            @NotNull final RowsCollector<S> collector) {
    String text = preprocess(sourceText);
    return new Query<S>(text, collector);
  }


  private String preprocess(@NotNull final String sourceText) {
    String text = sourceText;

    // check whether it is a source name
    if (text.startsWith("##")) {
      String sourceName = text.substring(2).trim();
      text = sources.get(sourceName);
      if (text == null) {
        throw new DBPreparingError("The source text named \"" + sourceName + "\" not loaded.");
      }
    }

    // ok
    return text;
  }
}
