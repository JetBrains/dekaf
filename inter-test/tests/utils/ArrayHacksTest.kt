package org.jetbrains.dekaf.inter.test.utils

import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.gears.expect
import org.jetbrains.dekaf.inter.utils.ArrayHacks
import org.jetbrains.dekaf.inter.utils.ArrayHacks.arrayClass
import org.junit.jupiter.api.Test


class ArrayHacksTest : UnitTest {

    @Test
    fun createMatrix() {
        val m: Array<Array<String>> = ArrayHacks.createArray(String::class.java, 5, 3)
        m[0][0] = "0:0"
        m[0][2] = "0:2"
        m[4][0] = "4:0"
        m[4][2] = "4:2"
    }


    @Test
    fun arrayClass_basic() {
        val a: Array<String> = arrayOf("S")
        val c = arrayClass(a)
        expect that c.isArray equalsTo true
        expect that c.componentType equalsTo java.lang.String::class.java
    }

}
