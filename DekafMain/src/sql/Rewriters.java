package org.jetbrains.dekaf.sql;

import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class Rewriters {

  public static Function<String,String> replace(@NotNull final String what, @NotNull final String with) {
    return arg -> StringsKt.replace(arg, what, with, false);
  }

  public static Function<String,String> replace(@NotNull final Map<String,String> map) {
    return arg -> {
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
    };
  }



}
