package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.Sqlite;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class SqlitePrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Sqlite() {
    assertThat(DB.rdbms()).isEqualTo(Sqlite.RDBMS);
  }


  @Override
  public void getConnectionInfo_user() {
    throw new AssumptionViolatedException("In Sqlite user can be empty");
  }
}
