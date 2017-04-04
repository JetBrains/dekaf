package org.jetbrains.dekaf;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.core.*;
import org.jetbrains.dekaf.jdbc.PostgresExceptionRecognizerTest;
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
