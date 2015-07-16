package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.*;
import org.jetbrains.jdba.jdbc.PostgreExceptionRecognizerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            PostgrePrimaryTest.class,
                            PostgreSessionTest.class,
                            PostgreCommandRunnerTest.class,
                            PostgreQueryRunnerTest.class,
                            PostgreExceptionRecognizerTest.class,
                            SudokuTest.class,
                            PostgreTestHelperTest.class
})
public class PostgreIntegrationTests {}
