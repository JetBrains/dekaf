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
    public String get(final @NotNull String parameterName,
                      final @NotNull String defaultValue) {
        String value = map.get(parameterName);
        return value != null ? value : defaultValue;
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
    public String toString() {
        StringBuilder b = new StringBuilder(size * 60);
        for (Map.Entry<String, String> entry : map.entrySet())
            b.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
        return b.toString();
    }
    
}
