package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.ClickHouse;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



@FixMethodOrder(MethodSorters.JVM)
public class CHousePrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_CHouse() {
    assertThat(DB.rdbms()).isEqualTo(ClickHouse.RDBMS);
  }


  @Override
  public void getConnectionInfo_user() {
    throw new AssumptionViolatedException("In ClickHouse user can be empty");
  }
}
