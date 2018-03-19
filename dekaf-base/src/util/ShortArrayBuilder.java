package org.jetbrains.dekaf.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public final class ShortArrayBuilder {

    private final List<short[]> arrays = new ArrayList<>();

    private int count = 0;


    public void addArray(short[] array) {
        if (array == null) return;
        int n = array.length;
        if (n == 0) return;
        arrays.add(array);
        count += n;
    }


    @NotNull
    public short[] buildArrayOfShort() {
        int n = arrays.size();
        switch (n) {
            case 0:  return emptyArrayOfShort;
            case 1:  return arrays.get(0);
            default: return buildIt();
        }
    }

    @NotNull
    private short[] buildIt() {
        final short[] array = new short[count];
        int p = 0;
        for (short[] a : arrays) {
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


    private static final short[] emptyArrayOfShort = new short[0];
}
