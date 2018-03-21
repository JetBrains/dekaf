package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;



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


    /**
     * Splits the given text by the specified delimiter.
     * @param text        text to split.
     * @param delimiter   the delimiter.
     * @param trim        whether to trim spaces.
     * @return            list of text parts.
     */
    @NotNull
    public static List<String> split(final @Nullable String text, char delimiter, boolean trim) {
        if (text == null) return emptyList();
        final int n = text.length();
        if (n == 0) return emptyList();

        int d = text.indexOf(delimiter);
        if (d < 0) {
            String item = trim ? text.trim() : text;
            return singletonList(item);
        }

        ArrayList<String> result = new ArrayList<>();
        int p = 0;
        while (true) {
            String item = text.substring(p, d);
            if (trim) item = item.trim();
            result.add(item);
            p = d + 1;
            if (p >= n) break;
            d = indexOfDelimiter(text, p, delimiter);
        }

        return result;
    }

    
    /**
     * Looks for the specified char in the given text.
     * Unlike the {@link String#indexOf}, it returns the length of the text when no such delimiter.
     * @param text        the text where to find a delimiter.
     * @param from        the index to start from (use 0 to start from the beginning of the text).
     * @param delimiter   the delimiter to find.
     * @return            index of the found delimiter, or length of text if not found.
     */
    public static int indexOfDelimiter(final @NotNull String text, int from, char delimiter) {
        int n = text.length();
        if (from == n) return n;
        if (from > n) throw new IllegalArgumentException("The 'from' position is too large: "+from+" when the text length is "+n+".");
        int index = text.indexOf(delimiter, from);
        return index >= 0 ? index : n;
    }


    /**
     * An empty array of strings.
     */
    public static final String[] NO_STRINGS = {};

}
