package org.jetbrains.jdba.core1;



import org.jetbrains.jdba.Rdbms;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class CommonIntegrationCase extends BaseIntegrationCase {

  protected final Rdbms rdbms = db.facade.rdbms();

  protected boolean isPostgre = rdbms.code.equalsIgnoreCase("postgre");
  protected boolean isOracle = rdbms.code.equalsIgnoreCase("oracle");

}
