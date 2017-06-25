package org.jetbrains.dekaf.text

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat


/**
 * @author Leonid Bushuev 
 */
internal class ScriptumResourceFromJavaTest {

    companion object {

        private var myResource: ScriptumResourceFromJava? = null

        @BeforeAll @JvmStatic
        fun setUpClass() {
            myResource = ScriptumResourceFromJava(ScriptumResourceFromJavaTest::class.java.classLoader,
                                                  "org/jetbrains/dekaf/text/ScriptumResourceFromJavaTest.sql")
        }
    }


    @Test
    fun load() {
        val count = myResource!!.count()
        assertThat(count).isGreaterThan(1)
    }

    @Test
    fun first() {
        val first = myResource!!["FIRST"]
        assertThat(first.text).isEqualTo("select something from anything")
    }

    @Test
    fun first_lower() {
        val first = myResource!!["first"]
        assertThat(first.text).isEqualTo("select something from anything")
    }

    @Test
    fun second() {
        val second = myResource!!["SECOND"]
        assertThat(second.text).isEqualTo("select 2")
    }

    @Test
    fun second_oracle() {
        val second = myResource!!["SECOND+ORACLE"]
        assertThat(second.text).isEqualTo("select 2 from dual")
    }

    @Test
    fun multi_line() {
        val expectedText = "select columns\n" +
                "from table\n" +
                "where the condition is met"
        val text = myResource!!["MULTI_LINE"]
        assertThat(text.text).isEqualTo(expectedText)
    }

    @Test
    fun lower_case() {
        val second = myResource!!["lower_case"]
        assertThat(second.text).isEqualTo("select something_lower_case")
    }

    @Test
    fun lower_case_in_upper_case() {
        val second = myResource!!["LOWER_CASE"]
        assertThat(second.text).isEqualTo("select something_lower_case")
    }

    @Test
    fun international_German() {
        val second = myResource!!["München_Straße"]
        assertThat(second.text).contains("etwas_von_München")
    }

    @Test
    fun international_Russian() {
        val second = myResource!!["Санкт-Петербург"]
        assertThat(second.text).contains("Невский Проспект")
    }

    @Test
    fun zero() {
        val second = myResource!!["0"]
        assertThat(second.text).contains("Some comment at the top of the file")
    }

    @Test
    fun the_end() {
        val second = myResource!!["THE_END"]
        assertThat(second.text).contains("select last_value")
    }

}