package org.jetbrains.dekaf;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.core.*;
import org.jetbrains.dekaf.jdbc.SybaseExceptionRecognizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            SybasePrimaryTest.class,
                            SybaseSessionTest.class,
                            SybaseCommandRunnerTest.class,
                            SybaseQueryRunnerTest.class,
                            SybaseExceptionRecognizerTest.class,
                            SybaseTestHelperTest.class
})
public class SybaseIntegrationTests {}
