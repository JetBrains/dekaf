package org.jetbrains.jdba.jdbc;

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
                            JdbcInterSessionTest.class,
                            JdbcInterSeanceSimpleTest.class,
                            JdbcInterCursorTest.class
})
public class JdbcUnitTests {}