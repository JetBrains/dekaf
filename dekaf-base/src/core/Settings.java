package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;



/**
 * Structured text-like settings.
 * Value object (immutable).
 */
public class Settings {

    /// STATE \\\

    @NotNull
    private final NavigableMap<String,String> map;

    private final int size;

    private transient int hash = 0;


    /// CONSTRUCTOR \\\

    public Settings(final @NotNull String... items) {
        final int n = items.length;
        if (n % 2 != 0) throw new IllegalArgumentException("Must be two arguments for every setting: name and value, but got " + n + " arguments");

        map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < n; i += 2) map.put(items[i], items[i + 1]);
        size = map.size();
    }

    public Settings(final @NotNull Map<String,String> map) {
        this(copyOf(map), false);
    }

    private Settings(@NotNull final NavigableMap<String, String> map, boolean copy) {
        this.map = copy ? copyOf(map) : map;
        this.size = this.map.size();
    }

    @NotNull
    private static NavigableMap<String,String> copyOf(final @NotNull Map<String,String> map) {
        if (map.isEmpty()) return Collections.emptyNavigableMap();
        TreeMap<String,String> newMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        newMap.putAll(map);
        return newMap;
    }


    /// ACCESSORS \\\

    @Nullable
    public String get(final @NotNull String parameterName) {
        return map.get(parameterName);
    }

    @NotNull
    public String get(final @NotNull String parameterName, final @NotNull String defaultValue) {
        String value = map.get(parameterName);
        return value != null ? value : defaultValue;
    }

    public boolean getBoolean(final @NotNull String parameterName, boolean defaultValue) {
        String string = map.get(parameterName);
        if (string == null || string.length() == 0) return defaultValue;
        try {
            boolean value = parseBoolean(string);
            return value;
        }
        catch (IllegalArgumentException iae) {
            throw new IllegalStateException("Cannot get parameter "+parameterName+" as boolean: "+iae.getMessage(), iae);
        }
    }

    @NotNull
    public Settings subSettings(final @NotNull String groupName) {
        return new Settings(subMap(groupName), false);
    }

    @NotNull
    private NavigableMap<String,String> subMap(final @NotNull String groupName) {
        String fromKey = groupName + '.';
        String tillKey = groupName + ".\uFFFF";
        return map.subMap(fromKey, true, tillKey, false);
    }

    @NotNull
    public Properties toProperties(final @Nullable Properties parentProperties) {
        return createProperties(parentProperties, map);
    }

    @NotNull
    public Properties toSubProperties(final @Nullable Properties parentProperties,
                                      final @NotNull String groupName) {
        return createProperties(parentProperties, subMap(groupName));
    }

    @NotNull
    private static Properties createProperties(final @Nullable Properties parentProperties,
                                               final @NotNull Map<String,String> map) {
        Properties properties = new Properties(parentProperties);
        for (Map.Entry<String, String> entry : map.entrySet())
            properties.setProperty(entry.getKey(), entry.getValue());
        return properties;
    }

    @NotNull
    public NavigableSet<String> getNames() {
        return map.navigableKeySet();
    }

    public int getSize() {
        return size;
    }



    /// OTHER \\\

    @Override
    public int hashCode() {
        int h = this.hash;
        if (h == 0 && size > 0) {
            for (Map.Entry<String, String> entry : map.entrySet())
                h = h * 11 + entry.getKey().hashCode() * 7 + entry.getKey().hashCode();
            if (h == 0) h = size;
            this.hash = h;
        }
        return h;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj instanceof Settings) return equals((Settings) obj);
        return false;
    }

    public boolean equals(final @NotNull Settings that) {
        if (this == that) return true;
        if (this.size != that.size) return false;
        if (this.hashCode() != that.hashCode()) return false;

        Iterator<Map.Entry<String, String>> it1 = this.map.entrySet().iterator();
        Iterator<Map.Entry<String, String>> it2 = that.map.entrySet().iterator();

        while (it1.hasNext() || it2.hasNext()) {
            if (!it1.hasNext()) return false;
            if (!it2.hasNext()) return false;
            Map.Entry<String, String> entry1 = it1.next();
            Map.Entry<String, String> entry2 = it2.next();
            if (String.CASE_INSENSITIVE_ORDER.compare(entry1.getKey(), entry2.getKey()) != 0) return false;
            if (!Objects.equals(entry1.getValue(), entry2.getValue())) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(size * 60);
        for (Map.Entry<String, String> entry : map.entrySet())
            b.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
        return b.toString();
    }


    /// HELPER FUNCTIONS \\\

    private static boolean parseBoolean(@NotNull String string) throws IllegalArgumentException {
        String s = string.trim();
        if (s.length() == 0) throw new IllegalArgumentException("Attempted to convert an empty string into a boolean");
        char c = Character.toUpperCase(s.charAt(0));
        switch (c) {
            case 'T':
            case 'Y':
            case 'Д':
            case '+':
            case '1':
                return true;
            case 'F':
            case 'N':
            case 'Н':                             
            case '-':
            case '0':
                return false;
            default:
                throw new IllegalArgumentException("Attempted to convert \""+s+"\" into a boolean");
        }
    }


    /// ZERO \\\

    public static final Settings NO_SETTINGS = new Settings(Collections.emptyNavigableMap(), false);

}
