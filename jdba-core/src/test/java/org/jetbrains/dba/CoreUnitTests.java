package org.jetbrains.dba;

import org.jetbrains.dba.core.StructRowFetcherTest;
import org.jetbrains.dba.core.ValueGetterTest;
import org.jetbrains.dba.jdbc.pooling.ConnectionPoolTest;
import org.jetbrains.dba.sql.*;
import org.jetbrains.dba.utils.NumberUtilsTest;
import org.jetbrains.dba.utils.StringsTest;
import org.jetbrains.dba.utils.VersionTest;
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
