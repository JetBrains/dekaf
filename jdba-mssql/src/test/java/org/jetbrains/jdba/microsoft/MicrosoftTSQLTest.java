package org.jetbrains.jdba.microsoft;

import org.jetbrains.jdba.junitft.FineRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;



@FixMethodOrder(MethodSorters.JVM)
@RunWith(FineRunner.class)
public class MicrosoftTSQLTest {


  @Test
  public void clone_basic() {
    MicrosoftTSQL sql1 = new MicrosoftTSQL();
    sql1.setCaseInsensitive(true);

    MicrosoftTSQL clone1 = sql1.clone();
    assertThat(clone1).isInstanceOf(MicrosoftTSQL.class);
    assertThat(clone1.isCaseInsensitive()).isTrue();

    sql1.setCaseInsensitive(false);

    assertThat(clone1.isCaseInsensitive()).isTrue(); // not changed

    MicrosoftTSQL clone2 = sql1.clone();
    assertThat(clone2.isCaseInsensitive()).isFalse();
  }


}