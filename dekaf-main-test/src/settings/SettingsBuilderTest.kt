package org.jetbrains.dekaf.mainTest.settings

import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.jetbrains.dekaf.inter.settings.Setting
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.main.settings.SettingsBuilder
import org.jetbrains.dekaf.main.settings.pair
import org.jetbrains.dekaf.main.settings.settingsOf
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.Test


class SettingsBuilderTest : UnitTest {

    @Test
    fun basic() {
        val b = SettingsBuilder()
        b.put("Good" to 44)
        b.put("Evil" to 13)
        b.put("None" to 99)

        val s = b.build()

        expect that s hasSize 3
        expect that s complies {
            at(0) complies {
                property { ::name } equalsTo "Good"
                property { ::value } equalsTo 44
            }
            at(1) complies {
                property { ::name } equalsTo "Evil"
                property { ::value } equalsTo 13
            }
            at(2) complies {
                property { ::name } equalsTo "None"
                property { ::value } equalsTo 99
            }
        }
    }


    @Test
    fun nestSettings_basic() {
        val b = SettingsBuilder()
        b.put("A" to 1)
        b.put("B" to settingsOf("B11" to 11, "B12" to 12))
        b.put("C" to 99)

        val s = b.build()

        expect that s hasSize 3
        expect that s.getEntry(1).name equalsTo "B"
        expect that s.getEntry(1).value iz Settings::class

        val nest = s.getEntry(1).nest()
        nest ?: failNull("nest()")

        expect that nest hasSize 2
        expect that nest.getEntry(0).name equalsTo "B11"
        expect that nest.getEntry(0).value equalsTo 11
    }


    @Test
    fun nestSettingsBuilder_basic() {
        val inner = SettingsBuilder()
        inner.put("B11" to 11)
        inner.put("B12" to 12)

        val outer = SettingsBuilder()
        outer.put("A" to 1)
        outer.put("B" to inner)
        outer.put("C" to 99)

        val s = outer.build()

        expect that s hasSize 3
        expect that s.getEntry(1).name equalsTo "B"
        expect that s.getEntry(1).value iz Settings::class

        val nest = s.getEntry(1).nest()
        nest ?: failNull("nest()")

        expect that nest hasSize 2
        expect that nest.getEntry(0).name equalsTo "B11"
        expect that nest.getEntry(0).value equalsTo 11
    }


    @Test
    fun empty() {
        val b = SettingsBuilder()

        b aka "SettingsBuilder" complies {
            property { ::isNotEmpty } iz false
            property { ::isEmpty } iz true
            property { ::size } iz zero
        }

        b.build() aka "Built Instance" sameAs Settings.empty
    }

    @Test
    fun empty_afterRemove() {
        val b = SettingsBuilder()
        b.put("First" to 1)
        b.put("Second" to 2)

        b.remove(1)
        b.remove("First")

        b aka "SettingsBuilder" complies {
            property { ::isNotEmpty } iz false
            property { ::isEmpty } iz true
            property { ::size } iz zero
        }

        b.build() aka "Built Instance" sameAs Settings.empty
    }


    @Test
    fun get_basic1() {
        val b = SettingsBuilder()
        b.put("First" to 11L)
        b.put("Second" to 22L)

        expect that b["First"] equalsTo 11L
        expect that b["Second"] equalsTo 22L
    }

    @Test
    fun get_basic2() {
        val b = SettingsBuilder()
        b.put("First" to 11L)
        b.put("Second" to 22L)

        expect that b[0].pair equalsTo ("First" to 11L)
        expect that b[1].pair equalsTo ("Second" to 22L)
    }


    @Test
    fun get_byPath_1() {
        val b = SettingsBuilder()
        b["first"] = "simpleValue"

        val path = arrayOf("first")
        expect that b[path] iz String::class equalsTo "simpleValue"
    }

    @Test
    fun get_byPath_2_innerIsSettings() {
        val b = SettingsBuilder()
        b["first"] = Settings(Setting("second", "theValue"))

        val path = arrayOf("first", "second")
        expect that b[path] iz String::class equalsTo "theValue"
    }

    @Test
    fun get_byPath_2_innerIsSettingsBuilder() {
        val inner = SettingsBuilder()
        inner["second"] = "theValue"

        val outer = SettingsBuilder()
        outer["first"] = inner

        val path = arrayOf("first", "second")
        expect that outer[path] iz String::class equalsTo "theValue"
    }

    @Test
    fun get_byPath_3() {
        val inner = SettingsBuilder()
        inner["second"] = Settings(Setting("third", "theSuperValue"))

        val outer = SettingsBuilder()
        outer["first"] = inner

        val path = arrayOf("first", "second", "third")
        expect that outer[path] iz String::class equalsTo "theSuperValue"
    }


    @Test
    fun set_byPath_1() {
        val b = SettingsBuilder()
        val path = arrayOf("name")
        b[path] = "value"

        expect that b aka "builder" complies {
            hasSize(1)
            at(0) equalsTo Setting("name", "value")
        }
    }

    @Test
    fun set_byPath_2() {
        val b = SettingsBuilder()
        val path1 = arrayOf("animal", "cat")
        val path2 = arrayOf("animal", "dog")
        b[path1] = "meow"
        b[path2] = "bark"

        val inner = b["animal"]
        inner aka "Inner" iz SettingsBuilder::class

        val b2 = inner as SettingsBuilder
        expect that b2 aka "Inner Builder" hasSize 2
        expect that b2["cat"] aka "cat value" equalsTo "meow"
        expect that b2["dog"] aka "dog value" equalsTo "bark"
    }


}