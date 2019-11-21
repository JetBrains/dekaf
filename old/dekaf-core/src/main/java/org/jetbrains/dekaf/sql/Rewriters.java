package org.jetbrains.dekaf.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.util.StringOperator;
import org.jetbrains.dekaf.util.Strings;

import java.util.Map;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class Rewriters {

  public static StringOperator replace(@NotNull final String what, @NotNull final String with) {
    return new StringOperator() {
      @Override
      public String apply(final String arg) {
        return Strings.replace(arg, what, with);
      }
    };
  }

  public static StringOperator replace(@NotNull final Map<String,String> map) {
    return new StringOperator() {
      @Override
      public String apply(final String arg) {
        if (map.isEmpty()) return arg;
        if (arg == null) return null;
        boolean was = false;
        StringBuilder b = new StringBuilder(arg);
        for (Map.Entry<String, String> entry : map.entrySet()) {
          String what = entry.getKey();
          String with = entry.getValue();
          int p = b.indexOf(what);
          while (p >= 0) {
            b.replace(p, p+what.length(), with);
            p = b.indexOf(what, p + with.length());
            was = true;
          }
        }
        return was ? b.toString() : arg;
      }
    };
  }



}
