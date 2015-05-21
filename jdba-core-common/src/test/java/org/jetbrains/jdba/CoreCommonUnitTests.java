package org.jetbrains.jdba;

/**
 * @author Leonid Bushuev from JetBrains
 */

import org.jetbrains.jdba.util.ArrayFunctionsTest;
import org.jetbrains.jdba.util.NumbersTest;
import org.jetbrains.jdba.util.StringsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



@RunWith(Suite.class)
@Suite.SuiteClasses({
                            NumbersTest.class,
                            StringsTest.class,
                            ArrayFunctionsTest.class
})
public class CoreCommonUnitTests {}
