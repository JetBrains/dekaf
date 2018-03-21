package org.jetbrains.dekaf.text

import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("UnitTest")
class TextPointerTest {

    @Test
    fun equals_true_onKotlin() {
        val p1 = TextPointer(999,1,2)
        val p2 = TextPointer(999,1,2)

        p1.equals(p2) expected true
        (p1 == p2) expected true
    }

    @Test
    @Suppress("platform_class_mapped_to_kotlin", "ReplaceCallWithComparison")
    fun equals_true_onJava() {
        val p1 = TextPointer(999,1,2)
        val p2 = TextPointer(999,1,2)

        val o1: java.lang.Object = p1 as Object
        val o2: java.lang.Object? = p2 as Object

        o1.equals(o2) expected true
    }

    @Test
    fun equals_false_onKotlin() {
        val p0 = TextPointer(999,1,2)
        val p1 = TextPointer(998,1,2)
        val p2 = TextPointer(999,7,2)
        val p3 = TextPointer(999,1,8)

        p0.equals(p1) expected false
        p0.equals(p2) expected false
        p0.equals(p3) expected false

        (p0 == p1) expected false
        (p0 == p2) expected false
        (p0 == p3) expected false
    }

    @Test
    @Suppress("platform_class_mapped_to_kotlin", "ReplaceCallWithComparison")
    fun equals_false_onJava() {
        val p0 = TextPointer(999,1,2)
        val p1 = TextPointer(998,1,2)
        val p2 = TextPointer(999,7,2)
        val p3 = TextPointer(999,1,8)

        val o0: java.lang.Object = p0 as Object
        val o1: java.lang.Object? = p1 as Object
        val o2: java.lang.Object? = p2 as Object
        val o3: java.lang.Object? = p3 as Object

        o0.equals(o1) expected false
        o0.equals(o2) expected false
        o0.equals(o3) expected false
    }

}