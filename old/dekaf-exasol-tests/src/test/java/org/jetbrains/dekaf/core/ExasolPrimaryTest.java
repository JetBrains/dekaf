package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.Exasol;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



@FixMethodOrder(MethodSorters.JVM)
public class ExasolPrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Exasol() {
    assertThat(DB.rdbms()).isEqualTo(Exasol.RDBMS);
  }


  @Override
  public void getConnectionInfo_user() {
    throw new AssumptionViolatedException("In Exasol user can be empty");
  }
}
