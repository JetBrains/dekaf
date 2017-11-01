package org.jetbrains.dekaf.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



class ArrayBuilderTest {

    @Test
    void buildArray_basic() {
        ArrayBuilder<Long> b = new ArrayBuilder<>(Long.class);
        b.addArray(new Long[] { 1111L, 2222L, 3333L });
        b.addArray(new Long[] { 4444L, 5555L, 6666L });
        Long[] array = b.buildArray();

        Assertions.assertArrayEquals(array, new Long[] { 1111L, 2222L, 3333L, 4444L, 5555L, 6666L });
    }

    @Test
    void buildList_basic() {
        ArrayBuilder<Long> b = new ArrayBuilder<>(Long.class);
        b.addArray(new Long[] { 1111L, 2222L, 3333L });
        b.addArray(new Long[] { 4444L, 5555L, 6666L });
        List<Long> list = b.buildList();

        Assertions.assertEquals(list, Arrays.asList(1111L, 2222L, 3333L, 4444L, 5555L, 6666L));
    }

    @Test
    void buildSet_basic() {
        ArrayBuilder<Long> b = new ArrayBuilder<>(Long.class);
        b.addArray(new Long[] { 1111L, 2222L, 3333L });
        b.addArray(new Long[] { 4444L, 5555L, 6666L });
        Set<Long> set = b.buildSet();

        Assertions.assertEquals(set, new HashSet<>(Arrays.asList(1111L, 2222L, 3333L, 4444L, 5555L, 6666L)));
    }

    @Test
    void buildSet_inputContainsDuplicates_1() {
        ArrayBuilder<Long> b = new ArrayBuilder<>(Long.class);
        b.addArray(new Long[] { 1111L, 2222L, 3333L, 5555L });
        b.addArray(new Long[] { 2222L, 4444L, 5555L, 6666L });
        Set<Long> set = b.buildSet();

        Assertions.assertEquals(set, new HashSet<>(Arrays.asList(1111L, 2222L, 3333L, 4444L, 5555L, 6666L)));
    }

    @Test
    void buildSet_inputContainsDuplicates_2() {
        ArrayBuilder<Long> b = new ArrayBuilder<>(Long.class);
        b.addArray(new Long[] { 1111L, 2222L, 2222L, 3333L });
        b.addArray(new Long[] { 4444L, 4444L, 5555L, 6666L });
        Set<Long> set = b.buildSet();

        Assertions.assertEquals(set, new HashSet<>(Arrays.asList(1111L, 2222L, 3333L, 4444L, 5555L, 6666L)));
    }

}