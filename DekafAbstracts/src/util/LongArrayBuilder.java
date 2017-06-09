package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public final class LongArrayBuilder {

    private final List<long[]> arrays = new ArrayList<>();

    private int count = 0;


    public void addArray(long[] array) {
        if (array == null) return;
        int n = array.length;
        if (n == 0) return;
        arrays.add(array);
        count += n;
    }


    @NotNull
    public long[] buildArrayOfLong() {
        int n = arrays.size();
        switch (n) {
            case 0:  return emptyArrayOfLong;
            case 1:  return arrays.get(0);
            default: return buildIt();
        }
    }

    @NotNull
    private long[] buildIt() {
        final long[] array = new long[count];
        int p = 0;
        for (long[] a : arrays) {
            int n = a.length;
            System.arraycopy(a, 0, array, p, n);
            p += n;
        }
        return array;
    }


    public int size() {
        return count;
    }


    public void clear() {
        arrays.clear();
        count = 0;
    }


    private static final long[] emptyArrayOfLong = new long[0];
}
