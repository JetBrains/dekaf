package org.jetbrains.jdba.core;

import org.jetbrains.jdba.jdbc.pooling.ConnectionPoolTest;
import org.jetbrains.jdba.sql.SQLScriptBuilderTest;
import org.jetbrains.jdba.sql.SQLScriptTest;
import org.jetbrains.jdba.sql.SQLTest;
import org.jetbrains.jdba.sql.ScriptumResourceFromJavaTest;
import org.jetbrains.jdba.utils.NumberUtilsTest;
import org.jetbrains.jdba.utils.StringsTest;
import org.jetbrains.jdba.utils.VersionTest;
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
                            // UTILS
                            NumberUtilsTest.class,
                            StringsTest.class,
                            VersionTest.class,
                            // SQL
                            ScriptumResourceFromJavaTest.class,
                            SQLTest.class,
                            SQLScriptBuilderTest.class,
                            SQLScriptTest.class,
                            // CORE
                            ValueGetterTest.class,
                            StructRowFetcherTest.class,
                            ConnectionPoolTest.class
})
public class CoreUnitTests {}
