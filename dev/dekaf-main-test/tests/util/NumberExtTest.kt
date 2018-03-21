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


    @Test
    fun Byte_toUnsignedInt() {
        //@formatter:off
        `1`    .toUnsignedInt() expected 1
        `127`  .toUnsignedInt() expected 127
        `-1`   .toUnsignedInt() expected 0xFF
        `-127` .toUnsignedInt() expected 0x81
        `-128` .toUnsignedInt() expected 0x80
        //@formatter:on
    }

    @Test
    fun Short_toUnsignedInt() {
        //@formatter:off
        `1s`      .toUnsignedInt() expected 1
        `32767s`  .toUnsignedInt() expected 32767
        `-1s`     .toUnsignedInt() expected 0xFFFF
        `-32767s` .toUnsignedInt() expected 0x8001
        `-32768s` .toUnsignedInt() expected 0x8000
        //@formatter:on
    }

}