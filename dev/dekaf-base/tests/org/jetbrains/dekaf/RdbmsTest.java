package org.jetbrains.dekaf;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;



@Tag("basic")
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



    private static class JustAClass implements Serializable {
        final Rdbms rdbms;
        private JustAClass(final Rdbms rdbms) {this.rdbms = rdbms;}
    }

    @Test
    void serialization() throws IOException, ClassNotFoundException {
        JustAClass c1 = new JustAClass(Rdbms.of("MicroDB"));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(c1);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(os.toByteArray()));
        Object gotObject = ois.readObject();
        assertThat(gotObject).isInstanceOf(JustAClass.class);

        JustAClass c2 = (JustAClass) gotObject;
        assertThat(c2.rdbms).isSameAs(c1.rdbms);
    }

}
