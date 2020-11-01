package org.jetbrains.dekaf.interTest.settings

import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.jetbrains.dekaf.inter.settings.Setting
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.Test


class SettingsTest : UnitTest {

    @Test
    fun get_byIndex() {
        val settings = Settings(Setting("name1", "value A"),
                                Setting("name2", "value B"),
                                Setting("name3", "value C"))
        expect that settings.getEntry(0) equalsTo Setting("name1", "value A")
        expect that settings.getEntry(1) equalsTo Setting("name2", "value B")
        expect that settings.getEntry(2) equalsTo Setting("name3", "value C")
        expect that settings.size equalsTo 3
    }

    @Test
    fun get_byName_3() {
        val settings = Settings(Setting("name1", "value A"),
                                Setting("name2", "value B"),
                                Setting("name3", "value C"))
        expect that settings.getEntry("name1") equalsTo Setting("name1", "value A")
        expect that settings.getEntry("name2") equalsTo Setting("name2", "value B")
        expect that settings.getEntry("name3") equalsTo Setting("name3", "value C")
    }

    @Test
    fun get_byName_12() {
        doTestByName(12)
    }

    @Test
    fun get_byName_44() {
        doTestByName(44)
    }

    private fun doTestByName(n: Int) {
        val entries = Array(n) { i -> Setting("name$i", "value $i") }
        val settings = Settings(*entries)
        for (i in 0 until n) {
            expect that settings.getEntry("name$i") sameAs entries[i]
        }
    }

    @Test
    fun toText_plain() {
        val settings = Settings.of("name1", "value A",
                                   "name2", "value B",
                                   "name3", "value C")
        val text = settings.toText().toString()
        val expectedText = """|name1 = value A
                              |name2 = value B
                              |name3 = value C
                           """.trimMargin() + '\n'
        expect that text equalsTo expectedText
    }

    @Test
    fun toText_nested() {
        val settings2 = Settings.of("name1", "value A",
                                    "name2", "value B",
                                    "name3", "value C")
        val settings1 = Settings.of("x", settings2,
                                    "y", "another value")
        val text = settings1.toText().toString()
        val expectedText = """|x.name1 = value A
                              |x.name2 = value B
                              |x.name3 = value C
                              |y = another value
                           """.trimMargin() + '\n'
        expect that text equalsTo expectedText
    }

    @Test
    fun iterator_basic() {
        val s1 = Setting("name1", "value A")
        val s2 = Setting("name2", "value B")
        val s3 = Setting("name3", "value C")
        val settings = Settings(s1, s2, s3)
        val it = settings.iterator()
        expect that it.next() sameAs s1
        expect that it.next() sameAs s2
        expect that it.next() sameAs s3
    }

}