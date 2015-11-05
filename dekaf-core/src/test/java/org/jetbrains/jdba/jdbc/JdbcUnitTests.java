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
                            JdbcJarProvidersTest.class,
                            JdbcSessionsTest.class,
                            ConnectionPoolTest.class,
                            JdbcIntermediateFacadeTest.class,
                            JdbcIntermediateSessionTest.class,
                            JdbcIntermediateSeanceSimpleTest.class,
                            JdbcIntermediateCursorTest.class,
                            UnknownDatabaseProviderTest.class,
                            JdbcIntermediateFederatedProviderTest.class
})
public class JdbcUnitTests {}