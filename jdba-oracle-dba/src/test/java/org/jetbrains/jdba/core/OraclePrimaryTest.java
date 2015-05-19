package org.jetbrains.jdba.core;

import org.jetbrains.annotations.NotNull;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@FixMethodOrder(MethodSorters.JVM)
public class OraclePrimaryTest extends CommonPrimaryTest {

  @NotNull
  @Override
  protected String fromSingleRowTable() {
    return " from dual";
  }
}
