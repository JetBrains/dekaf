package org.jetbrains.dekaf.util


import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


@Tag("UnitTest")
class TrivialCollectionsTest {

    @Test
    fun RepeatingList_basic() {
        val r = RepeatingList(999L, 3)

        r[0] expected 999L
        r[1] expected 999L
        r[2] expected 999L

        r.size      expected 3
        r.isEmpty() expected false

        r.indexOf(999L)     expected 0
        r.indexOf(888L)     expected -1
        r.lastIndexOf(999L) expected 0
        r.lastIndexOf(888L) expected -1
    }

    @Test
    fun RepeatingList_10() {
        val r = RepeatingList(1000L, 10)
        val s = r.subList(3,7)

        s.size expected 4
        s[0]   expected 1000L
    }

}