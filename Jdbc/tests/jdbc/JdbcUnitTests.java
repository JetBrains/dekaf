package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.jdbc.pooling.ConnectionPoolTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 */
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