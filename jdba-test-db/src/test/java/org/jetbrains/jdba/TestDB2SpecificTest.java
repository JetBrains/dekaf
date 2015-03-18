package org.jetbrains.jdba;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import testing.categories.ForMSSQL;
import testing.categories.ForOracle;



/**
 * @author Leonid Bushuev from JetBrains
 */
@FixMethodOrder(MethodSorters.JVM)
@Deprecated
public class TestDB2SpecificTest {

  @Test @Category({ForOracle.class, ForMSSQL.class})
  public void zapSchema() {
    TestDB2.zapSchema();
  }


}
