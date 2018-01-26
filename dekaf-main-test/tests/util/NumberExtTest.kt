package org.jetbrains.dekaf.util

import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("UnitTest")
class NumberExtTest {

    @Test
    fun Int_toFixString_0() {
        0.toFixString(1) expected "0"
        0.toFixString(2) expected "00"
        0.toFixString(9) expected "000000000"
    }

    @Test
    fun Int_toFixString_positive() {
        1.toFixString(1) expected "1"
        1.toFixString(2) expected "01"
        1.toFixString(3) expected "001"

        123.toFixString(1) expected "123"
        123.toFixString(2) expected "123"
        123.toFixString(3) expected "123"
        123.toFixString(4) expected "0123"
    }

    @Test
    fun Int_toFixString_negative() {
        (-1).toFixString(1) expected "-1"
        (-1).toFixString(2) expected "-01"
        (-1).toFixString(3) expected "-001"

        (-123).toFixString(1) expected "-123"
        (-123).toFixString(2) expected "-123"
        (-123).toFixString(3) expected "-123"
        (-123).toFixString(4) expected "-0123"
    }
    
}