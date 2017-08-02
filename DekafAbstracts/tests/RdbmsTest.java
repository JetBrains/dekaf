package org.jetbrains.dekaf;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;



class RdbmsTest {

    @Test
    void code() {
        Rdbms marker = Rdbms.of("THE_CODE");
        assertThat(marker.code).isEqualTo("THE_CODE");
    }

    @Test
    void reuseMarkerClass() {

        Rdbms marker1 = Rdbms.of("JUST_A_MARKER");
        Rdbms marker2 = Rdbms.of("JUST_A_MARKER");

        assertThat(marker2).isSameAs(marker1);
        assertThat(marker2.code).isSameAs(marker1.code);

    }

}
