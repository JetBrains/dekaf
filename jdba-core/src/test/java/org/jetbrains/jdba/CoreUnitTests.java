package org.jetbrains.jdba;

import org.jetbrains.jdba.core.StructRowFetcherTest;
import org.jetbrains.jdba.core.ValueGetterTest;
import org.jetbrains.jdba.jdbc.pooling.ConnectionPoolTest;
import org.jetbrains.jdba.sql.*;
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
                            NumberUtilsTest.class,
                            StringsTest.class,
                            VersionTest.class,
                            SQLTest.class,
                            SQLScriptBuilderTest.class,
                            SQLScriptTest.class,
                            OraSQLTest.class,
                            MicrosoftTSQLTest.class,
                            ValueGetterTest.class,
                            StructRowFetcherTest.class,
                            ConnectionPoolTest.class
})
public class CoreUnitTests {}
