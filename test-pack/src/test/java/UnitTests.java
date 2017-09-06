import org.jetbrains.dekaf.CoreUnitTests;
import org.jetbrains.dekaf.jdbc.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 **/


@RunWith(Suite.class)
@Suite.SuiteClasses({
    CoreUnitTests.class,
    JdbcUnitTests.class,
    OracleJdbcUnitTests.class,
    PostgresJdbcUnitTests.class,
    MssqlJdbcUnitTests.class,
    SybaseJdbcUnitTests.class,
    MysqlJdbcUnitTests.class,
    H2dbJdbcUnitTests.class,
    //RedshiftJdbcUnitTests.class,
    SqliteJdbcUnitTests.class,
    ExasolJdbcUnitTests.class
})
public class UnitTests {}
