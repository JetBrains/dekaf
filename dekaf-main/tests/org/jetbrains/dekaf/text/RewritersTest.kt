package org.jetbrains.dekaf.text

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*


@Tag("basic")
class RewritersTest {

    @Test
    fun replace_1_basic() {
        assertThat(Rewriters.replace("YYY", "TTT").apply("XXX YYY ZZZ")).isEqualTo("XXX TTT ZZZ")
    }

    @Test
    fun replace_1_same() {
        val str = "ABCDE"
        val result = Rewriters.replace("YYY", "TTT").apply(str)
        assertThat(result).isSameAs(str)
    }

    @Test
    fun replace_map_1() {
        assertThat(Rewriters.replace(Collections.singletonMap("YYY", "TTT")).apply("XXX YYY ZZZ"))
                .isEqualTo("XXX TTT ZZZ")
    }

    @Test
    fun replace_map_2x2() {
        val map = HashMap<String, String>()
        map.put("AAA", "X")
        map.put("BBB", "YYYYY")
        val original = "AAAAAABBBBBB"
        val expected = "XXYYYYYYYYYY"
        assertThat(Rewriters.replace(map).apply(original)).isEqualTo(expected)
    }

    @Test
    fun replace_map_same() {
        val original = "1234567890"
        assertThat(Rewriters.replace(Collections.singletonMap("YYY", "TTT")).apply(original))
                .isSameAs(original)
    }


}