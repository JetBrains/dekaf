package org.jetbrains.dekaf;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.core.*;
import org.jetbrains.dekaf.jdbc.MysqlExceptionRecognizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            MysqlPrimaryTest.class,
                            MysqlSessionTest.class,
                            MysqlCommandRunnerTest.class,
                            MysqlQueryRunnerTest.class,
                            MysqlExceptionRecognizerTest.class,
                            MysqlTestHelperTest.class,
                            MysqlVersionTest.class
})
public class MysqlIntegrationTests {}
