package org.jetbrains.jdba.core;

import org.jetbrains.jdba.Postgre;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class PostgrePrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Postgre() {
    assertThat(DB.rdbms()).isEqualTo(Postgre.RDBMS);
  }

}
