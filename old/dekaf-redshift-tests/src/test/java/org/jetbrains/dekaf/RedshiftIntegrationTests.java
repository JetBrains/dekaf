package org.jetbrains.dekaf;


import org.jetbrains.dekaf.core.RedshiftCommandRunnerTest;
import org.jetbrains.dekaf.core.RedshiftPrimaryTest;
import org.jetbrains.dekaf.core.RedshiftQueryRunnerTest;
import org.jetbrains.dekaf.core.RedshiftSessionTest;
import org.jetbrains.dekaf.jdbc.RedshiftExceptionRecognizingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
    RedshiftPrimaryTest.class,
    RedshiftSessionTest.class,
    RedshiftCommandRunnerTest.class,
    RedshiftQueryRunnerTest.class,
    RedshiftExceptionRecognizingTest.class
})
public class RedshiftIntegrationTests {}
