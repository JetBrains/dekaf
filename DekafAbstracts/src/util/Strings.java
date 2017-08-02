package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;



/**
 * Useful string functions.
 *
 * @author Leonid Bushuev
 */
public class Strings {


    @NotNull
    public static String ensureStartsWith(final @NotNull String str, final char c) {
        if (str.length() == 0) {
            return Character.toString(c);
        }
        else {
            char c1 = str.charAt(0);
            if (c1 == c) return str;
            else return c + str;
        }
    }


    @NotNull
    public static String ensureEndsWith(final @NotNull String str, final char c) {
        final int n = str.length();
        if (n > 0 && str.charAt(n - 1) == c) {
            return str;
        }
        else {
            return str + c;
        }
    }



    @NotNull
    public static String removeEnding(final @NotNull String str, final @NotNull String ending) {
        int n = str.length(),
            m = ending.length();
        return n > m && str.endsWith(ending) ? str.substring(0, n - m) : str;
    }


}
