package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.H2dbCommandRunnerTest;
import org.jetbrains.jdba.core.H2dbPrimaryTest;
import org.jetbrains.jdba.core.H2dbQueryRunnerTest;
import org.jetbrains.jdba.jdbc.H2dbExceptionRecognizingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            H2dbPrimaryTest.class,
                            H2dbCommandRunnerTest.class,
                            H2dbQueryRunnerTest.class,
                            H2dbExceptionRecognizingTest.class
})
public class H2dbIntegrationTests {}
