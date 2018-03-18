package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ExpectationDemoTest")
class DemoSuccessBasicTest {

    @Test
    fun `2 * 2 = 4`() {
        val x = 2 * 2
        x.must.be(4)
    }

    @Test
    fun `2 * 2 ≠ 5`() {
        val x = 2 * 2
        x.must.notBe(5)
    }


    @Test
    fun `value is Long (using generic)`() {
        val number: Number = 1234567890L
        number.must.beInstanceOf<Long>()
    }

    @Test
    fun `value is Long (using kotlin class)`() {
        val number: Number = 1234567890L
        number.must.beInstanceOf(Long::class)
    }

    @Test
    fun `value is Long (using java class)`() {
        val number: Number = 1234567890L
        number.must.beInstanceOf(java.lang.Long::class.java)
    }

    
}