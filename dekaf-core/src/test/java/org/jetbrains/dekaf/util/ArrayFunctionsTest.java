package org.jetbrains.dekaf.util;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.util.Collects.splitArrayPer;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class ArrayFunctionsTest {

  @Test
  public void splitArrayPer_empty() {
    String[][] packs = splitArrayPer(new String[0], 3);
    assertThat(packs).isNotNull()
                     .isEmpty();
  }

  @Test
  public void splitArrayPer_1() {
    final String[] origArray = {"A"};
    String[][] packs = splitArrayPer(origArray, 3);
    assertThat(packs).isNotNull()
                     .hasSize(1)
                     .containsOnly(origArray);
  }

  @Test
  public void splitArrayPer_3_3() {
    final String[] origArray = {"A","B","C"};
    String[][] packs = splitArrayPer(origArray, 3);
    assertThat(packs).isNotNull()
                     .hasSize(1)
                     .containsOnly(origArray);
  }

  @Test
  public void splitArrayPer_4_3() {
    final String[] origArray = {"A","B","C","D"};
    String[][] packs = splitArrayPer(origArray, 3);
    assertThat(packs).isNotNull()
                     .hasSize(2)
                     .containsExactly(new String[] {"A","B","C"}, new String[] {"D"});
  }

}