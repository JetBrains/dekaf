package org.jetbrains.dekaf.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Leonid Bushuev
 */
@Tag("basic")
class VersionTest {

    @Test
    void get_and_size() {
        Version v = Version.of(11,22,33,44);
        assertThat((Integer)v.get(0)).isEqualTo((Integer)11);
        assertThat((Integer)v.get(2)).isEqualTo((Integer)33);
        assertThat((Integer)v.size()).isEqualTo((Integer)4);
    }

    @Test
    void get_theRest() {
        Version v = Version.of(11,22,33);
        assertThat((Integer)v.get(3)).isEqualTo((Integer)0);
        assertThat((Integer)v.get(Short.MAX_VALUE)).isEqualTo((Integer)0);
    }

    @Test
    void toString_basic_0() {
        Version v = Version.of();
        assertThat(v.toString()).isEqualTo("0.0");
    }

    @Test
    void toString_basic_1() {
        Version v = Version.of(111);
        assertThat(v.toString()).isEqualTo("111.0");
    }

    @Test
    void toString_basic_3() {
        Version v = Version.of(111,222,333);
        assertThat(v.toString()).isEqualTo("111.222.333");
    }

    @Test
    void toString_basic_4_of_5() {
        Version v = Version.of(111,222,333,444,555);
        assertThat(v.toString(1, 4)).isEqualTo("111.222.333.444");
    }

    @ParameterizedTest
    @MethodSource("parse_basic$")
    void parse_basic(String original, String expected) {
        Version v = Version.of(original);
        assertThat(v.toString()).isEqualTo(expected);
    }

    private static String[][] parse_basic$() {
        return new String[][] {
            {"0", "0.0"},
            {"123456789", "123456789.0"},
            {"1.2.3", "1.2.3"},
            {"1,2", "1.2"},
            {"22.1-02", "22.1.2"},
            {"22.1_17", "22.1.17"},
            {"33.b1", "33.0"},
            {"1.2 my beta version", "1.2"},
            {"1.3 my beta version released 31.12.1995", "1.3"}
        };
    }

    @Test
    void parse_currentJavaVersion() {
        Version.of(System.getProperty("java.runtime.version"));
        // expecting no exceptions
    }


    @Test
    void compare_basic() {
        Version v = Version.of(1,6,22);
        Integer expected1 = +1;
        assertThat(v.compareTo(1, 6)).isEqualTo(expected1);
        assertThat(v.compareTo(1, 6, 22)).isEqualTo(0);
        Integer expected = -1;
        assertThat(v.compareTo(1, 7)).isEqualTo(expected);
    }

    @Test
    void compare_zeros() {
        Version v1 = Version.of(1,2,3),
            v2 = Version.of(1,2,3,0,0);
        assertThat(v1.compareTo(v2)).isEqualTo(0);
        assertThat(v2.compareTo(v1)).isEqualTo(0);
    }

    @Test
    void compare_bounds() {
        Version v1 = Version.of(1,1000),
                v2 = Version.of(1,Integer.MAX_VALUE),
                v3 = Version.of(1,Integer.MIN_VALUE);

        assertThat(v1.compareTo(v2)).isEqualTo(-1);
        assertThat(v2.compareTo(v1)).isEqualTo(+1);

        assertThat(v3.compareTo(v2)).isEqualTo(-1);
        assertThat(v2.compareTo(v3)).isEqualTo(+1);

        assertThat(v3.compareTo(v1)).isEqualTo(-1);
        assertThat(v1.compareTo(v3)).isEqualTo(+1);
    }

    @Test
    void equal_zeros() {
        Version v1 = Version.of(1,2,3),
                v2 = Version.of(1,2,3,0,0);
        assertThat(v2).isEqualTo(v1);
        assertThat(v1).isEqualTo(v2);
    }

    @Test
    void equal_zeros_and_empty() {
        Version v1 = Version.of(),
                v2 = Version.of(0,0,0);
        assertThat(v2).isEqualTo(v1);
        assertThat(v1).isEqualTo(v2);
    }

    @Test
    void hashCode_zeros() {
        Version v1 = Version.of(1,2,3),
                v2 = Version.of(1,2,3,0,0);
        assertThat(v2.hashCode()).isEqualTo(v1.hashCode());
    }


    @Test
    void isOrGreater_1() {
        assertThat(Version.of(1,2,3).isOrGreater(1,2,3)).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(1,2,2)).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(1,2)).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(1,1,7)).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(1)).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(0)).isTrue();

        assertThat(Version.of(1,2,3).isOrGreater(1,2,4)).isFalse();
        assertThat(Version.of(1,2,3).isOrGreater(1,3)).isFalse();
        assertThat(Version.of(1,2,3).isOrGreater(2)).isFalse();
    }

    @Test
    void isOrGreater_2() {
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(1,2,3))).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(1,2,2))).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(1,2))).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(1,1,7))).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(1))).isTrue();
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(0))).isTrue();

        assertThat(Version.of(1,2,3).isOrGreater(Version.of(1,2,4))).isFalse();
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(1,3))).isFalse();
        assertThat(Version.of(1,2,3).isOrGreater(Version.of(2))).isFalse();
    }

    @Test
    void less_1() {
        assertThat(Version.of(1,2,3).less(4)).isTrue();
        assertThat(Version.of(1,2,3).less(1)).isFalse();
    }

    @Test
    void less_2() {
        assertThat(Version.of(1,2,3).less(Version.of(4))).isTrue();
        assertThat(Version.of(1,2,3).less(Version.of(1))).isFalse();
    }

}