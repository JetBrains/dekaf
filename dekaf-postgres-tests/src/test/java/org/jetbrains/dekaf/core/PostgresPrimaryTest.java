package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.Postgres;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class PostgresPrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Postgres() {
    assertThat(DB.rdbms()).isEqualTo(Postgres.RDBMS);
  }

}
