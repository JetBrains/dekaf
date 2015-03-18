package org.jetbrains.jdba.postgre;

import org.jetbrains.jdba.core.BaseIntegrationCase;
import org.jetbrains.jdba.core.TestEnvironment;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class PostgreIntegrationCase extends BaseIntegrationCase {

  static {
    TestEnvironment.setup(new PostgreServiceFactory(), org.postgresql.Driver.class, null);
  }

}
