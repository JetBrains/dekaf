package org.jetbrains.jdba.core;

import org.jetbrains.jdba.jdbc.JdbcDataSource;



/**
 * @author Leonid Bushuev from JetBrains
 */
public abstract class BaseIntegrationCase {

  protected final DBServiceFactory myServiceFactory;

  protected final JdbcDataSource myDataSource;

  protected final DBFacade myFacade;



  protected BaseIntegrationCase() {
    myServiceFactory = TestEnvironment.getServiceFactory();
    myDataSource = new JdbcDataSource(TestEnvironment.getConnectionString(), null, TestEnvironment.getJdbcDriver());
    myFacade = myServiceFactory.createFacade(myDataSource);
  }
}
