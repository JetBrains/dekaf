package org.jetbrains.jdba.core;

import org.jetbrains.jdba.Mysql;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class MysqlPrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Mysql() {
    assertThat(DB.rdbms()).isEqualTo(Mysql.RDBMS);
  }

}
