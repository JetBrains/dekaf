import org.jetbrains.dba.access.StructRowFetcherTest;
import org.jetbrains.dba.access.ValueGetterTest;
import org.jetbrains.dba.sql.OraSQLTest;
import org.jetbrains.dba.sql.SQLScriptBuilderTest;
import org.jetbrains.dba.sql.SQLScriptTest;
import org.jetbrains.dba.sql.SQLTest;
import org.jetbrains.dba.utils.NumberUtilsTest;
import org.jetbrains.dba.utils.StringsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * @author Leonid Bushuev from JetBrains
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
// ------------------------------------------ \\
                      NumberUtilsTest.class,
                      StringsTest.class,
                      SQLTest.class,
                      SQLScriptBuilderTest.class,
                      SQLScriptTest.class,
                      OraSQLTest.class,
                      ValueGetterTest.class,
                      StructRowFetcherTest.class
// ------------------------------------------ \\
                    })
public class UnitTestsSuite {}
