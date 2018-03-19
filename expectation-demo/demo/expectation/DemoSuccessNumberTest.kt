package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ExpectationDemoTest")
class DemoSuccessNumberTest {

    @Test
    fun `int equals`() {
        0.must.be(0)
        (-2147483648).must.be(-2147483648)
        2147483647.must.be(2147483647)
    }

    @Test
    fun `long equals`() {
        0L.must.be(0L)
        Long.MIN_VALUE.must.be(Long.MIN_VALUE)
        Long.MAX_VALUE.must.be(Long.MAX_VALUE)
    }

    @Test
    fun `long range`() {
        val x = 22L
        x.must.beBetween(-100 .. +100).beNonZero 
    }

    @Test
    fun `long in a list of int`() {
        val x = 44L
        x.must.beIn(5,13,44,60)
    }

    @Test
    fun `long in a list of long`() {
        val x = 44L
        x.must.beIn(5L,13L,44L,60L)
    }

    @Test
    fun `long is greater`() {
        val x = 44L
        x.must.beGreater(13).beGreaterOrEqual(44)
    }

    @Test
    fun `long is less`() {
        val x = 44L
        x.must.beLess(77).beLessOrEqual(44)
    }

    @Test
    fun `long is positive`() {
        val x = 44L
        x.must.beNonZero.bePositiveOrZero.bePositive
    }

    @Test
    fun `long is negative`() {
        val x = -13L
        x.must.beNonZero.beNegativeOrZero.beNegative
    }

}