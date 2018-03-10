package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@Tag("ExpectationDemoTest")
class DemoDiffsTest {

    @Test
    fun byte_byte() {
        val actual: Byte = 13
        val expect: Byte = 44
        actual.must.be(expect)
    }


    @Test
    fun decimal_decimal_notSame() {
        val actual: BigDecimal = BigDecimal("1234567890")
        val expect: BigDecimal = BigDecimal("1234567890")
        actual.must.beSameAs(expect)
    }



}