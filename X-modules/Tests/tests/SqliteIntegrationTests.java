package org.jetbrains.dekaf;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.dekaf.core.SqliteCommandRunnerTest;
import org.jetbrains.dekaf.core.SqlitePrimaryTest;
import org.jetbrains.dekaf.core.SqliteQueryRunnerTest;
import org.jetbrains.dekaf.core.SqliteSessionTest;
import org.jetbrains.dekaf.jdbc.SqliteExceptionRecognizingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            SqlitePrimaryTest.class,
                            SqliteSessionTest.class,
                            SqliteCommandRunnerTest.class,
                            SqliteQueryRunnerTest.class,
                            SqliteExceptionRecognizingTest.class
})
public class SqliteIntegrationTests {}
