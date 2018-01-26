package org.jetbrains.dekaf.util

import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.assertions.expectedSameAs
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


@Tag("UnitTest")
class StringExtTest {

    @Test
    fun replace_nothing() {
        val origin = "Some string where nothing to replace"
        val result = origin.replace(what = "unexistent", with = "impossible")

        result expectedSameAs origin
    }

    @Test
    fun replace_basic() {
        val result = "A simple simple string".replace(what = "simple", with = "complex")
        result expected "A complex complex string"
    }

    @Test
    fun replace_specialCase() {
        val result = "000000000000".replace(what = "0000", with = "1230")
        result expected "123012301230"
    }

    
    @Test
    fun field_positive() {
        val string = "A___A:B___B:C___C"
        string.field(1, ':', "no") expected "A___A"
        string.field(2, ':', "no") expected "B___B"
        string.field(3, ':', "no") expected "C___C"
        string.field(4, ':', "no") expected "no"
    }

    @Test
    fun field_negative() {
        val string = "A___A:B___B:C___C"
        string.field(-1, ':', "no") expected "C___C"
        string.field(-2, ':', "no") expected "B___B"
        string.field(-3, ':', "no") expected "A___A"
        string.field(-4, ':', "no") expected "no"
    }

    @Test
    fun field_strange() {
        "A___A:B___B:C___C".field(0, ':', "default") expected "default"
    }

    @Test
    fun count_basic() {
        ".O.x.x.O.n.n.O.v.v.O.".countOf('O') expected 4
    }

    @Test
    fun count_other() {
        ".s.ss.".countOf('O') expected 0
        "".countOf('X') expected 0
    }

}