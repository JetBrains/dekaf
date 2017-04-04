package org.jetbrains.dekaf;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.core.*;
import org.jetbrains.dekaf.jdbc.MssqlExceptionRecognizerTest;
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
