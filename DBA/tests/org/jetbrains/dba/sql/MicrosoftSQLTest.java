package org.jetbrains.dba.sql;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import testing.junit.FineRunner;

import static org.assertj.core.api.Assertions.assertThat;



@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class MicrosoftSQLTest {


  @Test
  public void clone_basic() {
    MicrosoftSQL sql1 = new MicrosoftSQL();
    sql1.setCaseInsensitive(true);

    MicrosoftSQL clone1 = sql1.clone();
    assertThat(clone1).isInstanceOf(MicrosoftSQL.class);
    assertThat(clone1.isCaseInsensitive()).isTrue();

    sql1.setCaseInsensitive(false);

    assertThat(clone1.isCaseInsensitive()).isTrue(); // not changed

    MicrosoftSQL clone2 = sql1.clone();
    assertThat(clone2.isCaseInsensitive()).isFalse();
  }


}