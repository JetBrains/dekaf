package org.jetbrains.dekaf.sql;


import org.jetbrains.dekaf.text.Rewriters;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;



public class SqlCommandTest {

  @Test
  public void rewrite_basic() {
    SqlCommand cmd1 = new SqlCommand("AAA BBB CCC", 33, "name");
    SqlCommand cmd2 = cmd1.rewrite(Rewriters.INSTANCE.replace("BBB", "XXX"));

    assertThat(cmd2.getSourceText()).isEqualTo("AAA XXX CCC");

    assertThat(cmd2.getRow()).isEqualTo(33);
    assertThat(cmd2.getName()).isEqualTo("name");
  }

}