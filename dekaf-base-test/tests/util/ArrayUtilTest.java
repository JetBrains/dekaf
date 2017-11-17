package org.jetbrains.dekaf.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.util.ArrayUtil.*;



class ArrayUtilTest {

    @Test
    void chopAndPadArrayBy_exactly() {
        Number[] array = new Number[] { 111, 222, 333, 444 };
        Number[][] sausage = chopAndPadArrayBy(array, 4, null);
        assertThat(sausage[0]).isSameAs(array);
        assertThat(sausage.length).isEqualTo(1);
    }


    @Test
    void chopAndPadArrayBy_twoWholeSlices() {
        Number[] array = new Number[] { 111, 222, 333, 444, 555, 666, 777, 888 };
        Number[][] sausage = chopAndPadArrayBy(array, 4, null);
        Number[] slice0 = sausage[0];
        Number[] slice1 = sausage[1];
        assertThat(slice0).containsExactly(111, 222, 333, 444);
        assertThat(slice1).containsExactly(555, 666, 777, 888);
        assertThat(getArrayElementClass(slice0)).isSameAs(Number.class);
        assertThat(getArrayElementClass(slice1)).isSameAs(Number.class);
        assertThat(sausage.length).isEqualTo(2);
    }


    @Test
    void chopAndPadArrayBy_usual() {
        Number[] array = new Number[] { 111, 222, 333, 444, 555, 666 };
        Number[][] sausage = chopAndPadArrayBy(array, 4, null);
        Number[] slice0 = sausage[0];
        Number[] slice1 = sausage[1];
        assertThat(getArrayElementClass(slice0)).isSameAs(Number.class);
        assertThat(getArrayElementClass(slice1)).isSameAs(Number.class);
        assertThat(slice0).containsExactly(111, 222, 333, 444);
        assertThat(slice1[0]).isEqualTo(555);
        assertThat(slice1[1]).isEqualTo(666);
        assertThat(slice1[2]).isNull();
        assertThat(slice1[3]).isNull();
        assertThat(sausage.length).isEqualTo(2);
    }

    @Test
    void chopAndPadArrayBy_padding() {
        Number[] array = new Number[] { 111, 222, 333, 444, 555, 666 };
        Byte padding = (byte) -128;
        Number[][] sausage = chopAndPadArrayBy(array, 4, padding);
        Number[] slice0 = sausage[0];
        Number[] slice1 = sausage[1];
        assertThat(getArrayElementClass(slice0)).isSameAs(Number.class);
        assertThat(getArrayElementClass(slice1)).isSameAs(Number.class);
        assertThat(slice0).containsExactly(111, 222, 333, 444);
        assertThat(slice1[0]).isEqualTo(555);
        assertThat(slice1[1]).isEqualTo(666);
        assertThat(slice1[2]).isSameAs(padding);
        assertThat(slice1[3]).isSameAs(padding);
        assertThat(sausage.length).isEqualTo(2);
    }


    @Test
    void getArrayElementClass_basic() {
        Number[] array = new Number[] { 1, 2L };
        Class<Number> elementClass = getArrayElementClass(array);
        assertThat(elementClass).isSameAs(Number.class);
    }

    @Test
    void getArrayElementClass_emptyArray() {
        Number[] array = new Number[0];
        Class<Number> elementClass = getArrayElementClass(array);
        assertThat(elementClass).isSameAs(Number.class);
    }

    @Test
    void getArrayClass_emptyArray() {
        Number[] array = new Number[0];
        Class<Number[]> arrayClass = getArrayClass(array);
        assertThat(arrayClass.isArray()).isTrue();
        assertThat(arrayClass.getComponentType()).isSameAs(Number.class);
    }

}