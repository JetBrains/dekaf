package org.jetbrains.dekaf.mainTest.settings

import lb.yaka.expectations.*
import lb.yaka.gears.*
import org.jetbrains.dekaf.inter.settings.Setting
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.main.settings.SettingsLoader
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.Test


class SettingsLoaderTest : UnitTest {

    @Test
    fun basic1() {
        val text = "theName=theValue"
        val settings = makeSettings(text)

        expect that settings aka "Settings" complies {
            hasSize(1)
            at(0) equalsTo Setting("theName", "theValue")
        }
    }

    @Test
    fun basic1s() {
        val text = "theName = theValue "
        val settings = makeSettings(text)

        expect that settings aka "Settings" complies {
            hasSize(1)
            at(0) equalsTo Setting("theName", "theValue")
        }
    }

    @Test
    fun basic1nl() {
        val text = "theName=theValue\n\n"
        val settings = makeSettings(text)

        expect that settings aka "Settings" complies {
            hasSize(1)
            at(0) equalsTo Setting("theName", "theValue")
        }
    }


    @Test
    fun plain() {
        val text =
                """|cat = meow
                   |dog = bark
                   |duck = quack
                """.trimMargin()
        val settings = makeSettings(text)
        expect that settings hasSize 3
        expect that settings["cat"] equalsTo "meow"
        expect that settings["dog"] equalsTo "bark"
        expect that settings["duck"] equalsTo "quack"
    }

    @Test
    fun plain_commentsAtEnd() {
        val text =
                """|cat = meow    // comment 1
                   |dog = bark    // comment 2
                   |duck = quack  // comment 3
                """.trimMargin()
        val settings = makeSettings(text)
        expect that settings hasSize 3
        expect that settings["cat"] equalsTo "meow"
        expect that settings["dog"] equalsTo "bark"
        expect that settings["duck"] equalsTo "quack"
    }

    @Test
    fun plain_commentsBetween() {
        val text =
                """|// comment 1
                   |cat = meow 
                   |dog = bark    
                   |// comment 2
                   |duck = quack  
                   |// comment 3
                """.trimMargin()
        val settings = makeSettings(text)
        expect that settings hasSize 3
        expect that settings["cat"] equalsTo "meow"
        expect that settings["dog"] equalsTo "bark"
        expect that settings["duck"] equalsTo "quack"
    }

    @Test
    fun simple_level2() {
        val text =
                """|animal.cat = meow   
                   |animal.dog = bark   
                   |animal.duck = quack 
                """.trimMargin()
        val settings = makeSettings(text)

        expect that settings hasSize 1
        
        val inner = settings["animal"] as Settings
        expect that inner hasSize 3
        expect that inner["cat"] equalsTo "meow"
        expect that inner["dog"] equalsTo "bark"
        expect that inner["duck"] equalsTo "quack"
    }

    @Test
    fun nest_basic() {
        val text =
                """|animal:
                   |   cat = meow   
                   |   dog = bark   
                   |   duck = quack 
                """.trimMargin()
        val settings = makeSettings(text)

        expect that settings hasSize 1

        val inner = settings["animal"] as Settings
        expect that inner hasSize 3
        expect that inner["cat"] equalsTo "meow"
        expect that inner["dog"] equalsTo "bark"
        expect that inner["duck"] equalsTo "quack"
    }

    @Test
    fun nest_repeat() {
        val text =
                """|animal:
                   |   cat = meow
                   |animal:      
                   |   dog = bark   
                   |animal:      
                   |   duck = quack 
                """.trimMargin()
        val settings = makeSettings(text)

        expect that settings hasSize 1

        val inner = settings["animal"] as Settings
        expect that inner hasSize 3
        expect that inner["cat"] equalsTo "meow"
        expect that inner["dog"] equalsTo "bark"
        expect that inner["duck"] equalsTo "quack"
    }

    @Test
    fun nest_path() {
        val text =
                """|live.animal:
                   |   cat = meow   
                   |   dog = bark   
                   |   duck = quack 
                """.trimMargin()
        val settings = makeSettings(text)

        expect that settings hasSize 1

        val inner1 = settings["live"] as Settings
        expect that inner1 hasSize 1

        val inner2 = inner1["animal"] as Settings
        expect that inner2 hasSize 3
        expect that inner2["cat"] equalsTo "meow"
        expect that inner2["dog"] equalsTo "bark"
        expect that inner2["duck"] equalsTo "quack"
    }

    @Test
    fun nest_path_path() {
        val text =
                """|live.animal:
                   |   cat.voice = meow   
                   |   dog.voice = bark   
                   |   duck.voice = quack 
                """.trimMargin()
        val settings = makeSettings(text)

        expect that settings hasSize 1

        val inner1 = settings["live"] as Settings
        expect that inner1 hasSize 1

        val inner2 = inner1["animal"] as Settings
        expect that inner2 hasSize 3

        val catSettings = inner2["cat"] as Settings
        expect that catSettings["voice"] equalsTo "meow"

        val dogSettings = inner2["dog"] as Settings
        expect that dogSettings["voice"] equalsTo "bark"
    }

    @Test
    fun nest4() {
        val text =
                """|live:
                   |   animal:    
                   |       dog:    
                   |         voice = bark
                   |       cat:
                   |         voice = meow
                """.trimMargin()
        val settings = makeSettings(text)

        expect that settings hasSize 1

        val inner1 = settings["live"] as Settings
        expect that inner1 hasSize 1

        val inner2 = inner1["animal"] as Settings
        expect that inner2 hasSize 2

        val catSettings = inner2["cat"] as Settings
        expect that catSettings["voice"] equalsTo "meow"

        val dogSettings = inner2["dog"] as Settings
        expect that dogSettings["voice"] equalsTo "bark"
    }


    @Test
    fun quotes_baisc() {
        val text =
                """|single = ' in single quotes '
                   |double = " in double quotes "
                """.trimMargin()
        val settings = makeSettings(text)
        expect that settings["single"] equalsTo " in single quotes "
        expect that settings["double"] equalsTo " in double quotes "
    }

    @Test
    fun quotes_escapeQuotes() {
        val text =
                """|single = ' in \'single\' quotes '
                   |double = " in \"double\" quotes "
                """.trimMargin()
        val settings = makeSettings(text)
        expect that settings["single"] equalsTo " in 'single' quotes "
        expect that settings["double"] equalsTo " in \"double\" quotes "
    }

    @Test
    fun quotes_escapeSpecialCharacters() {
        val text =
                """|name = " text with \t and \r\n "
                """.trimMargin()
        val settings = makeSettings(text)
        expect that settings["name"] equalsTo " text with \t and \r\n "
    }



    private fun makeSettings(text: CharSequence): Settings {
        val loader = SettingsLoader()
        return loader.load(text)
    }

}