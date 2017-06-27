package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;



/**
 * Utility functions for working with arrays.
 */
public abstract class ArrayUtil {

    @NotNull
    public static <E> E[][] chopArrayBy(final @NotNull E[] array,
                                        final int sliceSize,
                                        final @Nullable E padding) {
        final int n = array.length;
        if (n == sliceSize) return createArrayOf(array);

        final int m = n / sliceSize; // number of whole slices
        final int r = n % sliceSize; // reminder — number of actual elements in the last slice
        final int q = r == 0 ? m : m + 1;

        final E[][] result = createArrayOf(getArrayClass(array), q);
        for (int i = 0; i < m; i++) {
            int offset = i * sliceSize;
            result[i] = Arrays.copyOfRange(array, offset, offset+sliceSize);
        }
        if (r > 0) {
            Class<E> elementClass = getArrayElementClass(array);
            int offset = m * sliceSize;
            E[] reminder = createArrayOf(elementClass, sliceSize);
            for (int i = 0; i < r; i++) reminder[i] = array[offset+i];
            for (int i = r; i < sliceSize; i++) reminder[i] = padding;
            result[m] = reminder;
        }

        return result;
    }


    /**
     * Creates an empty array of specified elements type.
     * @param elementClass   class of elements.
     * @param length         length of array to create.
     * @param <E>            type of elements.
     * @return               created array.
     */
    @NotNull @SuppressWarnings("unchecked")
    public static <E> E[] createArrayOf(final @NotNull Class<E> elementClass, final int length) {
        return (E[]) Array.newInstance(elementClass, length);
    }

    /**
     * Creates an array of one given element.
     * @param element the element of array.
     * @param <E>     the type of element.
     * @return        created array.
     */
    @NotNull @SuppressWarnings("unchecked")
    public static <E> E[] createArrayOf(final @NotNull E element) {
        E[] array = (E[]) Array.newInstance(element.getClass(), 1);
        array[0] = element;
        return array;
    }


    @NotNull @SuppressWarnings("unchecked")
    static <E> Class<E[]> getArrayClass(final @NotNull E[] array) {
        return (Class<E[]>) array.getClass();
    }

    @NotNull @SuppressWarnings("unchecked")
    static <E> Class<E> getArrayElementClass(final @NotNull E[] array) {
        return (Class<E>) array.getClass().getComponentType();
    }


}
