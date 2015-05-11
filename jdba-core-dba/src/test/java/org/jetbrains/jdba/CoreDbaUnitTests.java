package org.jetbrains.jdba;

import org.jetbrains.jdba.core.BaseQueryRunnerTest;
import org.jetbrains.jdba.core.BaseSessionTest;
import org.jetbrains.jdba.intermediate.AdaptIntermediateRdbmsProviderTest;
import org.jetbrains.jdba.sql.ScriptumBasicTest;
import org.jetbrains.jdba.sql.ScriptumResourceFromJavaTest;
import org.jetbrains.jdba.sql.SqlScriptBuilderTest;
import org.jetbrains.jdba.sql.SqlScriptTest;
import org.jetbrains.jdba.util.VersionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
                            // UTILS
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
                            BaseQueryRunnerTest.class
})
public class CoreDbaUnitTests {}
