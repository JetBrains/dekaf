package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.OracleCommandRunnerTest;
import org.jetbrains.jdba.core.OraclePrimaryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            OraclePrimaryTest.class,
                            OracleCommandRunnerTest.class
})
public class OracleIntegrationTests {}