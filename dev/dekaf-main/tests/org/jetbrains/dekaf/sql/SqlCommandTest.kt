package org.jetbrains.dekaf.sql


import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.dekaf.text.Rewriters
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


@Tag("basic")
class SqlCommandTest {

    @Test
    fun rewrite_basic() {
        val cmd1 = SqlCommand("AAA BBB CCC", 33, "name")
        val cmd2 = cmd1.rewrite(Rewriters.replace("BBB", "XXX"))

        assertThat(cmd2.sourceText).isEqualTo("AAA XXX CCC")

        assertThat(cmd2.row).isEqualTo(33)
        assertThat(cmd2.name).isEqualTo("name")
    }

}