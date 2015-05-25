package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 **/

import org.jetbrains.jdba.core.PostgreCommandRunnerTest;
import org.jetbrains.jdba.core.PostgrePrimaryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            PostgrePrimaryTest.class,
                            PostgreCommandRunnerTest.class
})
public class PostgreIntegrationTests {}