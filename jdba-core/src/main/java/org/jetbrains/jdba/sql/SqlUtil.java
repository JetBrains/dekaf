package org.jetbrains.jdba.sql;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.io.Resources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.utils.CaseInsensitiveStringComparator;

import java.io.IOException;
import java.net.URL;
import java.util.Map;



/**
 * @author Leonid Bushuev from JetBrains
 */
final class SQLUtil {
  @NotNull
  public static Map<String, String> loadResourceStatements(@NotNull final String resourceName) {
    final String text;
    try {
      URL url = Resources.getResource(resourceName);
      text = Resources.toString(url, Charsets.UTF_8);
    }
    catch (IOException ioe) {
      throw new RuntimeException("Failed to read resource " + resourceName, ioe);
    }

    final String[] statementsArray =
      text.split("\\n\\s*([;/]|go)+\\s*\\n");
    ImmutableMap.Builder<String, String> b =
      ImmutableSortedMap.orderedBy(CaseInsensitiveStringComparator.instance);
    for (String s : statementsArray) {
      String stmt = s.trim();
      if (stmt.length() == 0) {
        continue;
      }
      int p1 = stmt.indexOf("--#");
      if (p1 < 0) continue;
      int p2 = stmt.indexOf('\n', p1);
      if (p2 < 0) continue;
      String name = stmt.substring(p1 + 3, p2).trim();
      b.put(name, stmt);
    }

    return b.build();
  }
}
