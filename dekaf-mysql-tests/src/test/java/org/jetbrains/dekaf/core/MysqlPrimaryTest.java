package org.jetbrains.dekaf.core;

import org.jetbrains.dekaf.Mysql;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
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


  @Override
  @Test @Ignore
  public void zapSchema_foreignKeys() {
    //   http://stackoverflow.com/questions/4061293/mysql-cant-create-table-errno-150
  }
}
