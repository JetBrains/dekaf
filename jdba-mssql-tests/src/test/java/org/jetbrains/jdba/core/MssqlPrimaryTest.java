package org.jetbrains.jdba.core;

import org.jetbrains.jdba.Mssql;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class MssqlPrimaryTest extends CommonPrimaryTest {

  @Test
  public void rdbms_is_Mysql() {
    assertThat(DB.rdbms()).isEqualTo(Mssql.RDBMS);
  }


  @Override
  @Test @Ignore
  public void zapSchema_foreignKeys() {
    //   http://stackoverflow.com/questions/4061293/mysql-cant-create-table-errno-150
  }
}
