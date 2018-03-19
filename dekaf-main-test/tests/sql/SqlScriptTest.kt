package org.jetbrains.dekaf.sql


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


/**
 * @author Leonid Bushuev from JetBrains
 */
@Tag("UnitTest")
class SqlScriptTest {

    @Test
    fun construct_2() {
        val script1 = SqlScript("command1")
        val script2 = SqlScript("command2")
        val script = SqlScript(script1, script2)
        assertThat(script.statements.size).isEqualTo(2)
        assertThat(script.statements[0].sourceText).isEqualTo("command1")
        assertThat(script.statements[1].sourceText).isEqualTo("command2")
    }

    @Test
    fun toString_contains_all_commands() {
        val script = SqlScript("command 1", "command 2")
        val text = script.toString()
        assertThat(text).contains("command 1")
                .contains("command 2")
    }

}
