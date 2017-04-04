package org.jetbrains.dekaf;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.core.H2dbCommandRunnerTest;
import org.jetbrains.dekaf.core.H2dbPrimaryTest;
import org.jetbrains.dekaf.core.H2dbQueryRunnerTest;
import org.jetbrains.dekaf.core.H2dbSessionTest;
import org.jetbrains.dekaf.jdbc.H2dbExceptionRecognizingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            H2dbPrimaryTest.class,
                            H2dbSessionTest.class,
                            H2dbCommandRunnerTest.class,
                            H2dbQueryRunnerTest.class,
                            H2dbExceptionRecognizingTest.class
})
public class H2dbIntegrationTests {}
