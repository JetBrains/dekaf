package org.jetbrains.jdba.jdbc;

import org.jetbrains.jdba.jdbc.pooling.ConnectionPoolTest;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.categories.UnitTest;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(UnitTest.class)
@RunWith(Suite.class)
@Suite.SuiteClasses({
                            ConnectionPoolTest.class,
                            JdbcInterFacadeTest.class,
                            JdbcInterSessionTest.class,
                            JdbcInterSeanceSimpleTest.class,
                            JdbcInterCursorTest.class,
                            UnknownDatabaseServiceProviderTest.class,
                            JdbcInterFederatedServiceProviderTest.class
})
public class JdbcUnitTests {}