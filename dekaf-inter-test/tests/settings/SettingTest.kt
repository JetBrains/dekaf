package org.jetbrains.dekaf.interTest.settings

import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.jetbrains.dekaf.inter.settings.Setting
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.Test
import java.util.*


class SettingTest : UnitTest {

    @Test
    fun equals_basic() {
        val setting1 = Setting("theName", "theValue")
        val setting2 = Setting("the" + "Name", "the" + "Value")

        expect that (setting1.equals(setting2)) iz true
        expect that (setting1 == setting2) iz true
        expect that setting1 equalsTo setting2
    }

    @Test
    fun equals_viaObject() {
        val setting1 = Setting("theName", "theValue")
        val setting2 = Setting("the" + "Name", "the" + "Value")

        expect that Objects.equals(setting1, setting2) iz true
    }

}