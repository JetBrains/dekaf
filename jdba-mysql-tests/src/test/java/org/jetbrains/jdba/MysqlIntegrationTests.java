package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.MysqlCommandRunnerTest;
import org.jetbrains.jdba.core.MysqlPrimaryTest;
import org.jetbrains.jdba.core.MysqlQueryRunnerTest;
import org.jetbrains.jdba.jdbc.MysqlExceptionRecognizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            MysqlPrimaryTest.class,
                            MysqlCommandRunnerTest.class,
                            MysqlQueryRunnerTest.class,
                            MysqlExceptionRecognizerTest.class
})
public class MysqlIntegrationTests {}
