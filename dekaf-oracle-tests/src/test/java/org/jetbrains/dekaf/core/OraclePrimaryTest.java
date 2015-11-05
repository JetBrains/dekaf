package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.Oracle;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class OraclePrimaryTest extends CommonPrimaryTest {

  @NotNull
  @Override
  protected String fromSingleRowTable() {
    return " from dual";
  }


  @Test
  public void rdbms_is_Oracle() {
    assertThat(DB.rdbms()).isEqualTo(Oracle.RDBMS);
  }

}
