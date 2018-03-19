package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.jetbrains.dekaf.util.ArrayUtil.createArrayOf;



public final class ArrayBuilder<E> {

    @NotNull
    private final Class<E> elementClass;

    private final List<E[]> arrays = new ArrayList<>();

    private int count = 0;


    public ArrayBuilder(final @NotNull Class<E> elementClass) {
        this.elementClass = elementClass;
    }


    public void addArray(E[] array) {
        if (array == null) return;
        int n = array.length;
        if (n == 0) return;
        arrays.add(array);
        count += n;
    }

    public <T> void addArray(T[] array, Function<T,E> transform) {
        if (array == null) return;
        int n = array.length;
        if (n == 0) return;
        E[] array2 = createTheArray(n);
        for (int i = 0; i < n; i++) array2[i] = transform.apply(array[i]);
        arrays.add(array2);
        count += n;
    }


    @NotNull
    public E[] buildArray() {
        int n = arrays.size();
        switch (n) {
            case 0:  return createTheArray(0);
            case 1:  return arrays.get(0);
            default: return buildTheArray();
        }
    }

    @NotNull
    private E[] buildTheArray() {
        final E[] array = createTheArray(count);
        int p = 0;
        for (E[] a : arrays) {
            int n = a.length;
            System.arraycopy(a, 0, array, p, n);
            p += n;
        }
        return array;
    }

    private E[] createTheArray(int length) {
        return createArrayOf(elementClass, length);
    }


    public List<E> buildList() {
        if (count == 0) return Collections.emptyList();
        if (count == 1) return Collections.singletonList(arrays.get(0)[0]);

        ArrayList<E> list = new ArrayList<>(count);
        for (E[] array : arrays) list.addAll(Arrays.asList(array));
        return list;
    }


    public Set<E> buildSet() {
        if (count == 0) return Collections.emptySet();
        if (count == 1) return Collections.singleton(arrays.get(0)[0]);

        HashSet<E> set = new HashSet<>(count);
        for (E[] array : arrays) set.addAll(Arrays.asList(array));
        return set;
    }


    public void forEach(final @NotNull Consumer<E> consumer) {
        for (E[] array : arrays) {
            for (E element : array) {
                consumer.accept(element);
            }
        }
    }


    public int size() {
        return count;
    }


    public void clear() {
        arrays.clear();
        count = 0;
    }


}
