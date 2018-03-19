package org.jetbrains.dekaf.core;

import org.jetbrains.annotations.NotNull;



/**
 * @author Leonid Bushuev from JetBrains
 **/
public class OracleQueryRunnerTest extends CommonQueryRunnerTest {


  @NotNull
  @Override
  protected String sqlNow() {
    return "sysdate";
  }
}
