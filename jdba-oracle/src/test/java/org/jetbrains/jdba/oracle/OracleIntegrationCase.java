package org.jetbrains.jdba.oracle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jdba.core.BaseIntegrationCase;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class OracleIntegrationCase extends BaseIntegrationCase {

  @NotNull
  protected OracleTestHelper myHelper;


  protected OracleIntegrationCase() {
    myHelper = new OracleTestHelper(db.facade);
  }
}
