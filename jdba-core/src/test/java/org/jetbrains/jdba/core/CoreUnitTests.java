package org.jetbrains.jdba.core;

import org.jetbrains.jdba.sql.*;
import org.jetbrains.jdba.util.VersionTest;
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
                            VersionTest.class,
                            // SQL
                            ScriptumResourceFromJavaTest.class,
                            ScriptumBasicTest.class,
                            SQLTest.class,
                            SQLScriptBuilderTest.class,
                            SQLScriptTest.class,
                            // CORE
                            ValueGetterTest.class,
                            StructRowFetcherTest.class
})
public class CoreUnitTests {}
