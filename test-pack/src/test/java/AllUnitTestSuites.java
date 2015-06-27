/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.CoreUnitTests;
import org.jetbrains.jdba.jdbc.H2dbJdbcUnitTests;
import org.jetbrains.jdba.jdbc.MysqlJdbcUnitTests;
import org.jetbrains.jdba.jdbc.OracleJdbcUnitTests;
import org.jetbrains.jdba.jdbc.PostgreJdbcUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
    CoreUnitTests.class,
    OracleJdbcUnitTests.class,
    PostgreJdbcUnitTests.class,
    MysqlJdbcUnitTests.class,
    H2dbJdbcUnitTests.class
})
public class AllUnitTestSuites {}
