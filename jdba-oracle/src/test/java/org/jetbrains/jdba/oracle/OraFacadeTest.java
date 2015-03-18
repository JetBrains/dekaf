package org.jetbrains.jdba.oracle;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing.categories.ForOracle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.jdba.TestDB.FACADE;


@Category(ForOracle.class)
public class OraFacadeTest {

  @Test
  public void getRdbms_oracle() {
    assertThat(FACADE.rdbms()).isEqualTo(Oracle.RDBMS);
  }


}