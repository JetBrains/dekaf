package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.OracleCommandRunnerTest;
import org.jetbrains.jdba.core.OraclePrimaryTest;
import org.jetbrains.jdba.core.OracleQueryRunnerTest;
import org.jetbrains.jdba.core.OracleSessionTest;
import org.jetbrains.jdba.jdbc.OracleExceptionRecognizingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            OraclePrimaryTest.class,
                            OracleSessionTest.class,
                            OracleCommandRunnerTest.class,
                            OracleQueryRunnerTest.class,
                            OracleExceptionRecognizingTest.class
})
public class OracleIntegrationTests {}
