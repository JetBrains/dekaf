package org.jetbrains.dekaf;

import org.jetbrains.dekaf.CoreUnitTests;
import org.jetbrains.dekaf.jdbc.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
    CoreUnitTests.class,
    JdbcUnitTests.class
    //OracleJdbcUnitTests.class,
    //PostgresJdbcUnitTests.class,
    //MssqlJdbcUnitTests.class,
    //SybaseJdbcUnitTests.class,
    //MysqlJdbcUnitTests.class,
    //H2dbJdbcUnitTests.class,
    //SqliteJdbcUnitTests.class
})
public class UnitTests {}
