package org.jetbrains.dekaf.util;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.util.Strings.ensureEndsWith;
import static org.jetbrains.dekaf.util.Strings.ensureStartsWith;


@SuppressWarnings("SpellCheckingInspection")
public class StringsTest {


    /// ensureStartsWith and ensureEndsWith \\\

    @Test
    void ensureStartsWith_basic() {
        assertThat(ensureStartsWith("AAAA", 'C')).isEqualTo("CAAAA");
        assertThat(ensureStartsWith("CAAAA", 'C')).isEqualTo("CAAAA");
        assertThat(ensureStartsWith("", 'C')).isEqualTo("C");
    }

    @Test
    void ensureStartsWith_dontMakeNewInstanceIfAlreadyStarts() {
        final String str = "CAAA";
        final String ensured = ensureStartsWith(str, 'C');
        assertThat(ensured).isSameAs(str);
    }

    @Test
    void ensureEndsWith_basic() {
        assertThat(ensureEndsWith("AAAA", 'C')).isEqualTo("AAAAC");
        assertThat(ensureEndsWith("AAAAC", 'C')).isEqualTo("AAAAC");
        assertThat(ensureEndsWith("", 'C')).isEqualTo("C");
    }

    @Test
    void ensureEndsWith_dontMakeNewInstanceIfAlreadyEnds() {
        final String str = "AAAC";
        final String ensured = ensureEndsWith(str, 'C');
        assertThat(ensured).isSameAs(str);
    }


}
