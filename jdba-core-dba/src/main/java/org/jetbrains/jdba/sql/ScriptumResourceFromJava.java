package org.jetbrains.jdba.sql;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.util.Strings;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Locale;
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


  ScriptumResourceFromJava(@NotNull final ClassLoader classLoader,
                           @NotNull final String resourcePath) {
    myClassLoader = classLoader;
    myResourcePath = resourcePath;
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
    Map<String,String> map = new LinkedHashMap<String, String>();
    StringBuilder buf = new StringBuilder();
    String currentName = "0";
    while(true) {
      String line = reader.readLine();
      if (line == null) break;
      line = Strings.rtrim(line);
      Matcher m = SECTION_HEADER_PATTERN.matcher(line);
      if (m.matches()) {
        // first save the previous text
        putTheText(map, buf, currentName);
        // now start the new text
        buf.delete(0, buf.length());
        currentName = normalizeName(m);
      }
      else {
        buf.append(line).append('\n');
      }
    }
    putTheText(map, buf, currentName);
    myScripts = ImmutableMap.copyOf(map);
  }

  @SuppressWarnings("ConstantConditions")
  @NotNull
  private String normalizeName(final Matcher m) {
    return Strings.minimizeSpaces(m.group(1)).toUpperCase(Locale.GERMAN).replace(" + ", "+");
  }

  private void putTheText(final Map<String, String> map,
                          final StringBuilder buf,
                          final String currentName) {
    String text = buf.toString().trim();
    if (!map.containsKey(currentName)) map.put(currentName, text);
  }

}
