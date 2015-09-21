package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.*;
import org.jetbrains.jdba.jdbc.MysqlExceptionRecognizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            MysqlPrimaryTest.class,
                            MysqlSessionTest.class,
                            MysqlCommandRunnerTest.class,
                            MysqlQueryRunnerTest.class,
                            MysqlExceptionRecognizerTest.class,
                            MysqlTestHelperTest.class
})
public class MysqlIntegrationTests {}
