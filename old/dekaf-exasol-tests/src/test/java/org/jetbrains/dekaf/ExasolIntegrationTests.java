package org.jetbrains.dekaf;

import org.jetbrains.dekaf.core.ExasolCommandRunnerTest;
import org.jetbrains.dekaf.core.ExasolPrimaryTest;
import org.jetbrains.dekaf.core.ExasolQueryRunnerTest;
import org.jetbrains.dekaf.core.ExasolSessionTest;
import org.jetbrains.dekaf.jdbc.ExasolExceptionRecognizingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            ExasolPrimaryTest.class,
                            ExasolSessionTest.class,
                            ExasolCommandRunnerTest.class,
                            ExasolQueryRunnerTest.class,
                            ExasolExceptionRecognizingTest.class
})
public class ExasolIntegrationTests {}
