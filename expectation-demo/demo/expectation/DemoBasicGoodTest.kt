package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ExpectationDemoTest")
class DemoBasicGoodTest {

    @Test
    fun ok() {
        val x = 2 * 2
        x.must.be(4)
    }


}