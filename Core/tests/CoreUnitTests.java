package org.jetbrains.dekaf;

import org.jetbrains.dekaf.exceptions.DBExceptionTest;
import org.jetbrains.dekaf.sql.*;
import org.jetbrains.dekaf.util.ArrayFunctionsTest;
import org.jetbrains.dekaf.util.NumbersTest;
import org.jetbrains.dekaf.util.StringsTest;
import org.jetbrains.dekaf.util.VersionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                            // UTILS
                            NumbersTest.class,
                            StringsTest.class,
                            ArrayFunctionsTest.class,
                            DBExceptionTest.class,
                            VersionTest.class,
                            RewritersTest.class,
                            // SQL
                            RdbmsTest.class,
                            ScriptumResourceFromJavaTest.class,
                            ScriptumBasicTest.class,
                            SqlCommandTest.class,
                            SqlScriptBuilderTest.class,
                            SqlScriptTest.class,
})
public class CoreUnitTests {}
