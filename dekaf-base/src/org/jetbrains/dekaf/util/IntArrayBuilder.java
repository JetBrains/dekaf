package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public final class IntArrayBuilder {

    private final List<int[]> arrays = new ArrayList<>();

    private int count = 0;


    public void addArray(int[] array) {
        if (array == null) return;
        int n = array.length;
        if (n == 0) return;
        arrays.add(array);
        count += n;
    }


    @NotNull
    public int[] buildArrayOfInt() {
        int n = arrays.size();
        switch (n) {
            case 0:  return emptyArrayOfInt;
            case 1:  return arrays.get(0);
            default: return buildIt();
        }
    }

    @NotNull
    private int[] buildIt() {
        final int[] array = new int[count];
        int p = 0;
        for (int[] a : arrays) {
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


    private static final int[] emptyArrayOfInt = new int[0];
}
