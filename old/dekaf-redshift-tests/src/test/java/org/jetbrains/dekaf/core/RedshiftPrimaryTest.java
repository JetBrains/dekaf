package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.Redshift;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



@FixMethodOrder(MethodSorters.JVM)
public class RedshiftPrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Redshift() {
    assertThat(DB.rdbms()).isEqualTo(Redshift.RDBMS);
  }

}
