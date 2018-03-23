package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.Mssql;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class MssqlPrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Mssql() {
    assertThat(DB.rdbms()).isEqualTo(Mssql.RDBMS);
  }

}
