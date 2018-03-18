package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;



/**
 * Simple structured text-like settings.
 *
 * <p>
 *     Holds settings like:
 *     <pre>
 *         key = value
 *         group.key1 = value1
 *         group.key2 = value2
 *         group.subgroup.key3 = value3
 *         group.subgroup.key4 = value4
 *     </pre>
 *
 *     Both key and value are strings.
 * </p>
 *
 * <p>
 *     Keys are case insensitive.
 * </p>
 *
 * <p>
 *     This class has deep {@link #equals} method,
 *     in other words it compare the entire content.
 *     Also the {@link #hashCode()} method is lazy calculated and cached.
 * </p>
 *
 * Value object (immutable).
 */
public final class Settings {

    /// STATE \\\

    @NotNull
    private final NavigableMap<String,String> map;

    private final int size;

    /**
     * Lazy calculated and cached.
     * Value 0 means is not calculated yet.
     */
    private transient int hash = 0;


    /// CONSTRUCTOR \\\

    /**
     * Makes settings from the given items.
     *
     * Each couple of item is a key.
     *
     * Example:
     * <pre>
     *     Settings theSettings =
     *         new Settings(key1, value1,
     *                      key2, value2,
     *                      key3, value3);
     * </pre>
     *
     * @param items keys and values.
     *              All keys must be different (case insensitively).
     */
    public Settings(final String... items) {
        final int n = items.length;
        if (n % 2 != 0) throw new IllegalArgumentException("Must be two arguments for every setting: name and value, but got " + n + " arguments");

        map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < n; i += 2) map.put(items[i], items[i + 1]);
        size = map.size();
    }

    /**
     * Makes settings from the given map.
     * @param map map with settings; keys are case insensitive.
     */
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


    /**
     * Makes a new settings by overriding some of entries by given ones.
     *
     * For example:
     * <pre>
     *     Settings settings = new Settings ( "A", "1",
     *                                        "B", "2",
     *                                        "C", "3",
     *                                        "D", "4" );
     *     Settings newSettings = settings.override ( "A", null,  // remove entry A
     *                                                "B", "22",  // change entry B
     *                                                "E", "55" ) // append entry E
     * </pre>
     * The result is:
     * <ol>
     *     <li>"B" -> 22</li>
     *     <li>"C" -> 3</li>
     *     <li>"D" -> 4</li>
     *     <li>"E" -> 55</li>
     * </ol>
     *
     * @param items keys and values;
     *              keys must be different and not null, values can be nullable.
     * @return new settings.
     */
    @NotNull
    public Settings override(final String... items) {
        final int n = items.length;
        if (n % 2 != 0) throw new IllegalArgumentException("Must be two arguments for every setting: name and value, but got " + n + " arguments");

        if (items.length == 0) return this;
        if (size == 0) return new Settings(items);

        boolean modified = false;
        TreeMap<String,String> newMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        newMap.putAll(map);
        Object old;
        for (int i = 0; i < n; i += 2) {
            String key = items[i];
            if (key == null) throw new IllegalArgumentException("Null key");
            if (key.isEmpty()) throw new IllegalArgumentException("Empty key");

            String value = items[i + 1];
            if (value == null) {
                old = newMap.remove(key);
                modified |= old != null;
            }
            else {
                old = newMap.put(key, value);
                modified |= !Objects.equals(old, value);
            }
        }

        return modified ? new Settings(newMap, false) : this;
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
        if (groupName.length() == 0) throw new IllegalArgumentException("Empty group name");
        String fromKey = groupName + '.';
        String tillKey = groupName + ".\uFFFF";
        return map.subMap(fromKey, false, tillKey, false);
    }

    @NotNull
    public NavigableMap<String,String> toMap() {
        return Collections.unmodifiableNavigableMap(map);
    }

    @NotNull
    public Properties toProperties(final @Nullable Properties parentProperties) {
        return createProperties(parentProperties, map);
    }

    @NotNull
    public Properties toSubgroupProperties(final @Nullable Properties parentProperties,
                                           final @NotNull String groupName) {
        Properties newProperties = new Properties(parentProperties);
        int m = groupName.length() + 1;
        NavigableMap<String,String> subMap = subMap(groupName);
        for (Map.Entry<String, String> entry : subMap.entrySet())
            newProperties.setProperty(entry.getKey().substring(m), entry.getValue());
        return newProperties;
    }

    @NotNull
    private static Properties createProperties(final @Nullable Properties parentProperties,
                                               final @NotNull Map<String,String> map) {
        Properties newProperties = new Properties(parentProperties);
        for (Map.Entry<String, String> entry : map.entrySet())
            newProperties.setProperty(entry.getKey(), entry.getValue());
        return newProperties;
    }

    @NotNull
    public NavigableSet<String> getNames() {
        return map.navigableKeySet();
    }

    /**
     * Number of settings couples.
     */
    public int getSize() {
        return size;
    }

    /**
     * Whether the settings are empty or not.
     */
    public boolean isEmpty() {
        return size == 0;
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

    /**
     * An empty setting object.
     */
    public static final Settings NO_SETTINGS = new Settings(Collections.emptyNavigableMap(), false);

}
