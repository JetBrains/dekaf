package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;



/**
 * A simple serializable immutable map entry.
 * Both key an value are not-null.
 *
 *
 * @param <K>  type of the key.
 * @param <V>  type of the value.
 */
public final class SerializableMapEntry<K /*extends Serializable*/, V /*extends Serializable*/> implements Serializable {

    /**
     * Key.
     */
    @NotNull
    public final K key;

    /**
     * Value.
     */
    @NotNull
    public final V value;


    public SerializableMapEntry(final @NotNull K key, final @NotNull V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "->" + value;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }


    @Override @SuppressWarnings("unchecked")
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof SerializableMapEntry && this.equals((SerializableMapEntry<K,V>)obj);
    }

    public boolean equals(final SerializableMapEntry<K,V> that) {
        return this == that || this.key == that.key && this.value == that.value;
    }
}
