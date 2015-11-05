package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.*;
import org.jetbrains.jdba.jdbc.MssqlExceptionRecognizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            MssqlPrimaryTest.class,
                            MssqlSessionTest.class,
                            MssqlCommandRunnerTest.class,
                            MssqlQueryRunnerTest.class,
                            MssqlExceptionRecognizerTest.class,
                            MssqlTestHelperTest.class
})
public class MssqlIntegrationTests {}
