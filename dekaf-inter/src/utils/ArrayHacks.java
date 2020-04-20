package org.jetbrains.dekaf.inter.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;



public final class ArrayHacks {

    @SuppressWarnings("unchecked")
    public static <E> E[] createEmptyArray(final @NotNull Class<E> componentType) {
        return (E[]) Array.newInstance(componentType, 0);
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] createArray(final @NotNull Class<E> componentType, final int size) {
        return (E[]) Array.newInstance(componentType, size);
    }

    @SuppressWarnings("unchecked")
    public static <E> E[][] createArray(final @NotNull Class<E> componentType,
                                        final int outerSize,
                                        final int innerSize) {
        return (E[][]) Array.newInstance(componentType, outerSize, innerSize);
    }


    @SuppressWarnings("unchecked")
    public static <E> Class<E[]> arrayClass(E[] array) {
        return (Class<E[]>) array.getClass();
    }


}
