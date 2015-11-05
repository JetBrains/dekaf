package org.jetbrains.jdba;

import org.jetbrains.jdba.core.*;
import org.jetbrains.jdba.exceptions.DBExceptionTest;
import org.jetbrains.jdba.intermediate.AdaptIntermediateRdbmsProviderTest;
import org.jetbrains.jdba.sql.ScriptumBasicTest;
import org.jetbrains.jdba.sql.ScriptumResourceFromJavaTest;
import org.jetbrains.jdba.sql.SqlScriptBuilderTest;
import org.jetbrains.jdba.sql.SqlScriptTest;
import org.jetbrains.jdba.util.ArrayFunctionsTest;
import org.jetbrains.jdba.util.NumbersTest;
import org.jetbrains.jdba.util.StringsTest;
import org.jetbrains.jdba.util.VersionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                            // UTILS
                            NumbersTest.class,
                            StringsTest.class,
                            ArrayFunctionsTest.class,
                            DBExceptionTest.class,
                            VersionTest.class,
                            // SQL
                            ScriptumResourceFromJavaTest.class,
                            ScriptumBasicTest.class,
                            SqlScriptBuilderTest.class,
                            SqlScriptTest.class,
                            // Intermediate Layer
                            AdaptIntermediateRdbmsProviderTest.class,
                            // Base client functionality
                            BaseSessionTest.class,
                            BaseQueryRunnerDirectTest.class,
                            BaseQueryRunnerPseudoRemoteTest.class,
                            BaseFacadeTest.class,
                            BaseRdbmsProviderTest.class,
                            BaseFederatedProviderTest.class
})
public class CoreUnitTests {}
