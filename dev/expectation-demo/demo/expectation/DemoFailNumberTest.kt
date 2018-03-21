package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ExpectationDemoTest")
class DemoFailNumberTest {

    @Test
    fun long_equal_int() {
        1234567890123456789L.must.be(2112454933) // must fail
    }


    @Test
    fun long_range_min() {
        val x = 22L
        x.must.beBetween(44 .. 77) // fail
    }

    @Test
    fun long_range_max() {
        val x = 2222L
        x.must.beBetween(44 .. 77) // fail
    }


    @Test
    fun int_zero() {
        0.must.beZero
        (-33).must.beZero
    }

    @Test
    fun int_nonZero() {
        9.must.beNonZero
        0.must.beNonZero
    }

    @Test
    fun int_positive() {
        1234.must.bePositive
        (-33).must.bePositive
    }

    @Test
    fun int_negative() {
        (-33).must.beNegative
        (+33).must.beNegative
    }


    @Test
    fun float_tolerance() {
        val e = 2.718281828f
        val x = 2.71828f
        x.must.be(e, 0.1f)
              .be(e, 0.001f)
              .be(e, 0.00001f)   // ok
              .be(e, 0.0000001f) // fail
    }

    @Test
    fun double_tolerance() {
        val e = 2.71828182845904523536
        val x = 2.718281828
        x.must.be(e, 0.1)
              .be(e, 0.0001)
              .be(e, 0.00000001)     // ok
              .be(e, 0.000000000001) // fail
    }

}