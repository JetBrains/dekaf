package org.jetbrains.dekaf;

import org.junit.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;



public class RdbmsTest {

  @SuppressWarnings("RedundantStringConstructorCall")
  @Test
  public void dedulplicate_on_create() {
    String str1 = new String("FantasticDB");
    String str2 = new String("FantasticDB");
    assertThat(str2).isNotSameAs(str1);
    Rdbms rdbms1 = Rdbms.of(str1);
    Rdbms rdbms2 = Rdbms.of(str2);
    assertThat(rdbms2).isSameAs(rdbms1);
  }


  private static final class JustAClass implements Serializable {
    public final Rdbms rdbms;

    private JustAClass(final Rdbms rdbms) {
      this.rdbms = rdbms;
    }
  }

  @Test
  public void serialization() throws IOException, ClassNotFoundException {
    JustAClass c1 = new JustAClass(Rdbms.of("MicroDB"));

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(os);
    oos.writeObject(c1);

    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(is);
    Object gotObject = ois.readObject();

    assertThat(gotObject).isInstanceOf(JustAClass.class);
    JustAClass c2 = (JustAClass) gotObject;

    assertThat(c2.rdbms).isSameAs(c1.rdbms);
  }

}