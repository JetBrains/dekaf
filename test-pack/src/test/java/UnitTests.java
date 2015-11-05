/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.CoreUnitTests;
import org.jetbrains.dekaf.jdbc.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
    CoreUnitTests.class,
    JdbcUnitTests.class,
    OracleJdbcUnitTests.class,
    PostgresJdbcUnitTests.class,
    MssqlJdbcUnitTests.class,
    MysqlJdbcUnitTests.class,
    H2dbJdbcUnitTests.class
})
public class UnitTests {}
