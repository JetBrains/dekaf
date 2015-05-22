package org.jetbrains.jdba.sql;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.util.Strings;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class ScriptumResourceFromJava extends ScriptumResource {

  private static final Pattern SECTION_HEADER_PATTERN =
          Pattern.compile("^\\s*-{4,}\\s*(([\\p{L}\\p{javaJavaIdentifierPart}$-]+)(\\s*\\+\\s*([\\p{L}\\p{javaJavaIdentifierPart}$-]+))?)\\s*-{4,}\\s*$",
                          Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

  @NotNull
  private final ClassLoader myClassLoader;

  @NotNull
  private final String myResourcePath;

  @NotNull
  private final String myFileName;


  ScriptumResourceFromJava(@NotNull final ClassLoader classLoader,
                           @NotNull final String resourcePath) {
    myClassLoader = classLoader;
    myResourcePath = resourcePath;
    myFileName = Strings.lastPart(resourcePath, '/');
  }


  @Override
  protected void load() {
    try {
      final InputStream stream = openStream();
      try {
        Reader reader1 = new InputStreamReader(stream, Charsets.UTF_8);
        try {
          BufferedReader reader2 = new BufferedReader(reader1);
          try {
            loadScripts(reader2);
          }
          finally {
            reader2.close();
          }
        }
        finally {
          reader1.close();
        }
      }
      finally {
        stream.close();
      }
    }
    catch (IOException ioe) {
      throw new RuntimeException(String.format("cannot load resource  %s: %s", myResourcePath, ioe.getMessage()));
    }

  }

  @NotNull
  protected InputStream openStream() {
    return myClassLoader.getResourceAsStream(myResourcePath);
  }


  private void loadScripts(@NotNull final BufferedReader reader) throws IOException {
    Map<String,TextFileFragment> map = new LinkedHashMap<String, TextFileFragment>();
    StringBuilder buf = new StringBuilder();
    String currentName = "0";
    int row = 0;
    int fragmentRow = 1;
    while(true) {
      String line = reader.readLine();
      row++;

      if (line == null) break;
      line = Strings.rtrim(line);
      Matcher m = SECTION_HEADER_PATTERN.matcher(line);
      if (m.matches()) {
        // first save the previous text
        putTheText(map, buf, fragmentRow, currentName);
        // now start the new text
        fragmentRow = row+1;
        buf.delete(0, buf.length());
        currentName = normalizeName(m);
      }
      else if (line.length() == 0 && buf.length() == 0) {
        fragmentRow++; // skipping empty lines before command text
      }
      else {
        buf.append(line).append('\n');
      }
    }
    putTheText(map, buf, fragmentRow, currentName);
    myScripts = ImmutableMap.copyOf(map);
  }

  @SuppressWarnings("ConstantConditions")
  @NotNull
  private String normalizeName(final Matcher m) {
    return Strings.minimizeSpaces(m.group(1)).replace(" + ", "+");
  }

  private void putTheText(final Map<String, TextFileFragment> map,
                          final StringBuilder buf,
                          final int row,
                          final String name) {
    String text = Strings.rtrim(buf.toString());
    if (!map.containsKey(name)) {
      TextFileFragment fragment = new TextFileFragment(text, myFileName, row, 1, name);
      map.put(name, fragment);
    }
  }

}
