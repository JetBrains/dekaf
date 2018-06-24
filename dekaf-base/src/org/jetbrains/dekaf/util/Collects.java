package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;



/**
 * Yet another collection utils: static functions for working with collections.
 */
public class Collects {

    /**
     * Copies the given collection to an immutable serializable one.
     * @param collection the collection to copy.
     * @param <E>        type of an element.
     * @return           an immutable serializable copy.
     */
    @NotNull
    public static <E> Collection<E> copy(@NotNull Collection<E> collection) {
        int n = collection.size();
        switch (n) {
            case 0: return Collections.emptySet();
            case 1: return Collections.singleton(collection.iterator().next());
            default: return Collections.unmodifiableCollection(new ArrayList<>(collection));
        }
    }


    /**
     * Copies the given collection to an immutable serializable one,
     * or return null if the given collection is null or empty.
     * @param collection the collection to copy.
     * @param <E>        type of an element.
     * @return           an immutable serializable copy with elements, or null.
     */
    @Nullable
    public static <E> Collection<E> copyOrNull(@Nullable Collection<E> collection) {
        if (collection == null || collection.isEmpty()) return null;
        else return copy(collection);
    }



}
