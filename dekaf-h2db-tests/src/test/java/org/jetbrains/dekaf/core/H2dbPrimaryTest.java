package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.H2db;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class H2dbPrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_H2db() {
    assertThat(DB.rdbms()).isEqualTo(H2db.RDBMS);
  }

}
