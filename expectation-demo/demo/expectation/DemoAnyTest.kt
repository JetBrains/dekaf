package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ExpectationDemoTest")
class DemoAnyTest {

    @Test
    fun checkForNull() {
        val x: Any? = null
        val y: Any? = Object()
        x.must.beNull
        y.must.beNull
    }

    @Test
    fun checkForNotNull() {
        val x: Object? = Object()
        val y: Object? = null
        x.must.beNotNull
        y.must.beNotNull
    }

    @Test
    fun instanceOf() {
        val x: Number = 1234567890123456789L
        x.must.beInstanceOf<Short>()
    }

    @Test
    fun predicate1() {
        val x = 123
        x.must.satisfy { it > 0 }
        x.must.satisfy { it < 0 }
    }

    @Test
    fun predicate2() {
        val x = 123
        x.must.satisfy("positive") { it > 0 }
        x.must.satisfy("negative") { it < 0 }
    }

    @Test
    fun true1() {
        true.must.beTrue
        false.must.beTrue
    }

    @Test
    fun true2() {
        true.must.be(true)
        false.must.be(true)
    }

    @Test
    fun true3() {
        false.must.be(java.lang.Boolean.FALSE)
        false.must.be(java.lang.Boolean.TRUE)
    }

    @Test
    fun in1() {
        val x = 44
        x.must.beIn(5,13,44,72)
        x.must.beIn(1,5,13,22,76,99)
    }

    @Test
    fun in2() {
        val x = 44
        x.must.beIn(setOf(5,13,44,72))
        x.must.beIn(setOf(1,5,13,22,76,99))
    }

}


