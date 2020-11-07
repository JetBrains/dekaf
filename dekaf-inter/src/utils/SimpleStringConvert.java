package org.jetbrains.dekaf.inter.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public abstract class SimpleStringConvert {

    @NotNull
    public static String escapeJavaString(@NotNull String string) {
        int n = string.length();
        if (n == 0) return string;
        StringBuilder b = null;
        for (int i = 0; i < n; i++) {
            char c = string.charAt(i);
            String r = null;
            switch (c) {
                case '\t': r = "\\t"; break;
                case '\b': r = "\\b"; break;
                case '\n': r = "\\n"; break;
                case '\r': r = "\\r"; break;
                case '\f': r = "\\f"; break;
                case '\"': r = "\\\""; break;
                case '\\': r = "\\\\"; break;
            }
            if (r != null) {
                if (b == null) {
                    b = new StringBuilder(n + 10);
                    b.append(string, 0, i);
                }
                b.append(r);
            }
            else {
                if (b != null) b.append(c);
            }
        }
        return b != null ? b.toString() : string;
    }

    @NotNull
    public static List<String> importStringList(@NotNull CharSequence text) {
        ArrayList<String> container = new ArrayList<>();
        int n = text.length(), k = 0;
        while (k < n) {
            char c = text.charAt(k);
            if (Character.isWhitespace(c)) {
                k++;
            }
            else if (c == ',') {
                // superfluous comma
                k++;
            }
            else if (c == '\r' || c == '\n') {
                // superfluous line break
                k++;
            }
            else if (c == '"' || c == '\'') {
                k = parseQuotedStringItemToList(text, k, container);
            }
            else {
                k = parseSimpleStringItemToList(text, k, container);
            }
        }
        return container;
    }

    private static int parseSimpleStringItemToList(final CharSequence text,
                                                   final int offset,
                                                   final ArrayList<String> container) {
        int n = text.length(), k = offset, m = offset;
        while (k < n) {
            char c = text.charAt(k);
            k++;
            if (c == ',' || c == '\r' || c == '\n') break;
            if (!Character.isWhitespace(c)) m = k;
        }
        String s = text.subSequence(offset, m).toString();
        container.add(s);
        return k;
    }

    private static int parseQuotedStringItemToList(final CharSequence text,
                                                   final int offset,
                                                   final ArrayList<String> container) {
        char q = text.charAt(offset);
        int n = text.length(), k = offset + 1;
        while (k < n) {
            char c = text.charAt(k);
            k++;
            if (c == q) break;
            if (c == '\n') break;
        }
        if (k - 1 < offset + 1) return Math.max(k, offset + 1);
        String s = text.subSequence(offset + 1, k - 1).toString();
        container.add(s);
        return k;
    }

}
