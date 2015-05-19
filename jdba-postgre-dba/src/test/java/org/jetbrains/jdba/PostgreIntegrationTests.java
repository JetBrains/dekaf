package org.jetbrains.jdba;


import org.jetbrains.jdba.core.PostgrePrimaryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 **/
@RunWith(Suite.class)
@Suite.SuiteClasses({
                            PostgrePrimaryTest.class
})
public class PostgreIntegrationTests {
}
