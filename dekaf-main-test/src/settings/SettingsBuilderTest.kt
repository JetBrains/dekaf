package org.jetbrains.dekaf.mainTest.settings

import lb.yaka.expectations.*
import lb.yaka.gears.*
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
        expect that s[1].name equalsTo "B"
        expect that s[1].value iz Settings::class

        val nest = s[1].nest()
        nest ?: failNull("nest()")

        expect that nest hasSize 2
        expect that nest[0].name equalsTo "B11"
        expect that nest[0].value equalsTo 11
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
        expect that s[1].name equalsTo "B"
        expect that s[1].value iz Settings::class

        val nest = s[1].nest()
        nest ?: failNull("nest()")

        expect that nest hasSize 2
        expect that nest[0].name equalsTo "B11"
        expect that nest[0].value equalsTo 11
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


}