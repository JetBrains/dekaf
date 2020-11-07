package org.jetbrains.dekaf.inter.settings;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

import static org.jetbrains.dekaf.inter.utils.SimpleStringConvert.escapeJavaString;



/**
 * Settings.
 */
public final class Settings implements Iterable<Setting>, Serializable {

    @NotNull
    private final Setting[] entries;

    @Nullable
    private final transient Map<String, Setting> map;


    @NotNull
    public static Settings of(@NotNull String name1, @NotNull Serializable value1) {
        Setting setting1 = new Setting(name1, value1);
        return new Settings(setting1);
    }

    @NotNull
    public static Settings of(@NotNull String name1, @NotNull Serializable value1,
                              @NotNull String name2, @NotNull Serializable value2) {
        Setting setting1 = new Setting(name1, value1);
        Setting setting2 = new Setting(name2, value2);
        return new Settings(setting1, setting2);
    }

    @NotNull
    public static Settings of(@NotNull String name1, @NotNull Serializable value1,
                              @NotNull String name2, @NotNull Serializable value2,
                              @NotNull String name3, @NotNull Serializable value3) {
        Setting setting1 = new Setting(name1, value1);
        Setting setting2 = new Setting(name2, value2);
        Setting setting3 = new Setting(name3, value3);
        return new Settings(setting1, setting2, setting3);
    }


    @NotNull
    public static final Settings empty = new Settings();


    public Settings(final @NotNull Setting... entries) {
        this.entries = entries;
        int n = entries.length;
        if (n <= 5) {
            map = null;
        }
        else {
            map = n <= 16 ? new TreeMap<>() : new HashMap<>();
            for (int i = 0; i < n; i++) {
                Setting entry = entries[i];
                map.put(entry.name, entry);
            }
        }
    }


    public int getSize() {
        return entries.length;
    }

    @NotNull
    public Setting get(int index)
            throws IndexOutOfBoundsException
    {
        return entries[index];
    }

    @Nullable
    public Setting get(@NotNull String name) {
        if (map != null) {
            return map.get(name);
        }
        else {
            for (Setting entry : entries) {
                if (entry.name.equals(name)) return entry;
            }
            return null;
        }
    }

    @Nullable
    public Serializable getValue(@NotNull String name) {
        Setting entry = get(name);
        return entry != null ? entry.value : null;
    }

    @Nullable
    public String getString(@NotNull String name) {
        Setting entry = get(name);
        return entry != null ? entry.getAsString() : null;
    }

    @Nullable
    public String[] getStrings(@NotNull String name) {
        Setting entry = get(name);
        return entry != null ? entry.getAsStrings() : null;
    }

    @Nullable
    public List<String> getStringList(@NotNull String name) {
        Setting entry = get(name);
        return entry != null ? entry.getAsStringList() : null;
    }

    @Nullable
    public Settings getNest(@NotNull String name) {
        Setting entry = get(name);
        return entry != null ? entry.nest() : null;
    }

    @Nullable
    public Properties getNestAsProperties(@NotNull String name) {
        Settings nest = getNest(name);
        return nest != null ? nest.toProperties() : null;
    }

    
    @NotNull
    public Properties toProperties() {
        Properties properties = new Properties();
        for (Setting entry : entries) properties.put(entry.name, entry.value);
        return properties;
    }


    @NotNull
    @Override
    public Iterator<Setting> iterator() {
        return Arrays.asList(entries).iterator();
    }

    @Override
    public Spliterator<Setting> spliterator() {
        return Arrays.spliterator(entries);
    }


    @NotNull
    public CharSequence toText() {
        StringBuilder buff = new StringBuilder();
        exportToText(buff, null);
        return buff;
    }

    private void exportToText(@NotNull StringBuilder buff, @Nullable String prefix) {
        String newPrefix = null;
        for (Setting entry : entries) {
            String name = entry.name;
            Settings nest = entry.nest();
            if (nest != null) {
                if (newPrefix == null)
                    newPrefix = prefix != null ? prefix + name + '.' : name + '.';
                nest.exportToText(buff, newPrefix);
            }
            else {
                if (prefix != null) buff.append(prefix);
                buff.append(name)
                    .append(" = ")
                    .append(escapeJavaString(entry.value.toString()))
                    .append('\n');
            }
        }
    }

    @Override
    public String toString() {
        return toText().toString();
    }
    
}
