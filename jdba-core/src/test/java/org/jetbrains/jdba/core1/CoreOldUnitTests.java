package org.jetbrains.jdba.core1;

import org.jetbrains.jdba.sql.SQLTest;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testing.categories.UnitTest;



/**
 * @author Leonid Bushuev from JetBrains
 */
@Category(UnitTest.class)
@RunWith(Suite.class)
@Suite.SuiteClasses({
                            // SQL
                            SQLTest.class,
                            // CORE
                            ValueGetterTest.class,
                            StructRowFetcherTest.class
})
public class CoreOldUnitTests {}
