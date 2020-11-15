package org.jetbrains.dekaf.inter.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static org.jetbrains.dekaf.inter.utils.SimpleStringConvert.importStringList;



/**
 * One setting: a pair of name and value.
 */
public final class Setting implements Serializable {

    @NotNull
    public final String name;

    @NotNull
    public final Serializable value;

    public Setting(@NotNull final String name, @NotNull final Serializable value) {
        assert name.length() > 0 : "Name of a setting cannot be empty";
        assert name.trim().equals(name) : "Name of a setting cannot have trailing spaces";
        this.name = name;
        this.value = value;
    }


    /**
     * If this setting is a nest of settings, returns them.
     * @return nested settings; or null if this is a single setting.
     */
    @Nullable
    public Settings nest() {
        if (value instanceof Settings) return (Settings) value;
        else return null;
    }


    @NotNull
    public String getAsString() {
        return value.toString();
    }

    @NotNull
    public String[] getAsStrings() {
        CharSequence text = value instanceof CharSequence ? (CharSequence) value : value.toString();
        return importStringList(text).toArray(new String[0]);
    }

    @NotNull
    public List<String> getAsStringList() {
        CharSequence text = value instanceof CharSequence ? (CharSequence) value : value.toString();
        return importStringList(text);
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o instanceof Setting) return equals((Setting)o);
        return false;
    }

    public boolean equals(final @NotNull Setting that) {
        return this.name.equals(that.name) && Objects.equals(this.value, that.value);
    }

    @Override
    public String toString() {
        return name + " = " + value.toString();
    }
    
}
