package org.jetbrains.dekaf;

import org.jetbrains.dekaf.core.CHouseCommandRunnerTest;
import org.jetbrains.dekaf.core.CHousePrimaryTest;
import org.jetbrains.dekaf.core.CHouseQueryRunnerTest;
import org.jetbrains.dekaf.core.CHouseSessionTest;
import org.jetbrains.dekaf.jdbc.CHouseExceptionRecognizingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            CHousePrimaryTest.class,
                            CHouseSessionTest.class,
                            CHouseCommandRunnerTest.class,
                            CHouseQueryRunnerTest.class,
                            CHouseExceptionRecognizingTest.class
})
public class CHouseIntegrationTests {}
