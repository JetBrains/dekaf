package org.jetbrains.dekaf.sql;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;



@SuppressWarnings("SpellCheckingInspection")
public class RewritersTest {

  @Test
  public void replace_1_basic() {
    assertThat(Rewriters.replace("YYY","TTT").apply("XXX YYY ZZZ")).isEqualTo("XXX TTT ZZZ");
  }

  @Test
  public void replace_1_same() {
    String str = "ABCDE";
    String result = Rewriters.replace("YYY", "TTT").apply(str);
    assertThat(result).isSameAs(str);
  }

  @Test
  public void replace_map_1() {
    assertThat(Rewriters.replace(Collections.singletonMap("YYY", "TTT")).apply("XXX YYY ZZZ"))
        .isEqualTo("XXX TTT ZZZ");
  }

  @Test
  public void replace_map_2x2() {
    Map<String,String> map = new HashMap<String, String>();
    map.put("AAA", "X");
    map.put("BBB", "YYYYY");
    String original = "AAAAAABBBBBB";
    String expected = "XXYYYYYYYYYY";
    assertThat(Rewriters.replace(map).apply(original)).isEqualTo(expected);
  }

  @Test
  public void replace_map_same() {
    String original = "1234567890";
    assertThat(Rewriters.replace(Collections.singletonMap("YYY", "TTT")).apply(original))
        .isSameAs(original);
  }



}