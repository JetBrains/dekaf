package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.*;
import org.jetbrains.jdba.jdbc.PostgresExceptionRecognizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            PostgresPrimaryTest.class,
                            PostgresSessionTest.class,
                            PostgresCommandRunnerTest.class,
                            PostgresQueryRunnerTest.class,
                            PostgresExceptionRecognizerTest.class,
                            SudokuTest.class,
                            PostgresTestHelperTest.class
})
public class PostgresIntegrationTests {}
