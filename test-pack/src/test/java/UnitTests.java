/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.CoreUnitTests;
import org.jetbrains.jdba.jdbc.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
    CoreUnitTests.class,
    JdbcUnitTests.class,
    OracleJdbcUnitTests.class,
    PostgreJdbcUnitTests.class,
    MysqlJdbcUnitTests.class,
    H2dbJdbcUnitTests.class
})
public class UnitTests {}
