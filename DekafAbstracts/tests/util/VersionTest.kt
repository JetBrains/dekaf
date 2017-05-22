package org.jetbrains.dekaf.util

import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Test


/**
 * @author Leonid Bushuev from JetBrains
 */
class VersionTest {

    @Test
    fun get_and_size() {
        val v = Version.of(11, 22, 33, 44)
        v[0] expected 11
        v[1] expected 22
        v[2] expected 33
        v[3] expected 44
        v.size() expected 4
    }

    @Test
    fun get_theRest() {
        val v = Version.of(11, 22, 33)
        v[3] expected 0
        v[4] expected 0
    }

    @Test
    fun toString_basic_0() {
        val v = Version.of()
        v.toString() expected "0.0"
    }

    @Test
    fun toString_basic_1() {
        val v = Version.of(111)
        v.toString() expected "111.0"
    }

    @Test
    fun toString_basic_3() {
        val v = Version.of(111, 222, 333)
        v.toString() expected "111.222.333"
    }

    @Test
    fun toString_basic_4_of_5() {
        val v = Version.of(111, 222, 333, 444, 555)
        v.toString(1,4) expected "111.222.333.444"
    }

    /*
    @TestWithParams(params = "POSSIBLE_VERSION_STRINGS")
    fun parse_basic(original: String, expected: String) {
        val v = Version.of(original)
        Assertions.assertThat(v.toString()).isEqualTo(expected)
    }

    @Test
    fun parse_currentJavaVersion() {
        Version.of(System.getProperty("java.runtime.version"))
        // expecting no exceptions
    }


    @Test
    fun compare_basic() {
        val v = Version.of(1, 6, 22)
        val expected1 = +1
        Assertions.assertThat(v.compareTo(1, 6)).isEqualTo(expected1)
        Assertions.assertThat(v.compareTo(1, 6, 22)).isEqualTo(0)
        val expected = -1
        Assertions.assertThat(v.compareTo(1, 7)).isEqualTo(expected)
    }

    @Test
    fun compare_zeros() {
        val v1 = Version.of(1, 2, 3)
        val v2 = Version.of(1, 2, 3, 0, 0)
        Assertions.assertThat(v1.compareTo(v2)).isEqualTo(0)
        Assertions.assertThat(v2.compareTo(v1)).isEqualTo(0)
    }

    @Test
    fun compare_bounds() {
        val v1 = Version.of(1, 1000)
        val v2 = Version.of(1, Integer.MAX_VALUE)
        val v3 = Version.of(1, Integer.MIN_VALUE)

        Assertions.assertThat(v1.compareTo(v2)).isEqualTo(-1)
        Assertions.assertThat(v2.compareTo(v1)).isEqualTo(+1)

        Assertions.assertThat(v3.compareTo(v2)).isEqualTo(-1)
        Assertions.assertThat(v2.compareTo(v3)).isEqualTo(+1)

        Assertions.assertThat(v3.compareTo(v1)).isEqualTo(-1)
        Assertions.assertThat(v1.compareTo(v3)).isEqualTo(+1)
    }

    @Test
    fun equal_zeros() {
        val v1 = Version.of(1, 2, 3)
        val v2 = Version.of(1, 2, 3, 0, 0)
        Assertions.assertThat(v2).isEqualTo(v1)
        Assertions.assertThat(v1).isEqualTo(v2)
    }

    @Test
    fun equal_zeros_and_empty() {
        val v1 = Version.of()
        val v2 = Version.of(0, 0, 0)
        Assertions.assertThat(v2).isEqualTo(v1)
        Assertions.assertThat(v1).isEqualTo(v2)
    }

    @Test
    fun hashCode_zeros() {
        val v1 = Version.of(1, 2, 3)
        val v2 = Version.of(1, 2, 3, 0, 0)
        Assertions.assertThat(v2.hashCode()).isEqualTo(v1.hashCode())
    }


    @Test
    fun isOrGreater_1() {
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(1, 2, 3)).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(1, 2, 2)).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(1, 2)).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(1, 1, 7)).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(1)).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(0)).isTrue()

        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(1, 2, 4)).isFalse()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(1, 3)).isFalse()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(2)).isFalse()
    }

    @Test
    fun isOrGreater_2() {
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(1, 2, 3))).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(1, 2, 2))).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(1, 2))).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(1, 1, 7))).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(1))).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(0))).isTrue()

        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(1, 2, 4))).isFalse()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(1, 3))).isFalse()
        Assertions.assertThat(Version.of(1, 2, 3).isOrGreater(Version.of(2))).isFalse()
    }

    @Test
    fun less_1() {
        Assertions.assertThat(Version.of(1, 2, 3).less(4)).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).less(1)).isFalse()
    }

    @Test
    fun less_2() {
        Assertions.assertThat(Version.of(1, 2, 3).less(Version.of(4))).isTrue()
        Assertions.assertThat(Version.of(1, 2, 3).less(Version.of(1))).isFalse()
    }

    companion object {

        private val POSSIBLE_VERSION_STRINGS = arrayOf(
            arrayOf("0", "0.0"),
            arrayOf("123456789", "123456789.0"),
            arrayOf("1.2.3", "1.2.3"),
            arrayOf("1,2", "1.2"),
            arrayOf("22.1-02", "22.1.2"),
            arrayOf("22.1_17", "22.1.17"),
            arrayOf("33.b1", "33.0"),
            arrayOf("1.2 my beta version", "1.2"),
            arrayOf("1.3 my beta version released 31.12.1995", "1.3"))
    }
    */

}