package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.dekaf.H2db;
import org.jetbrains.dekaf.Rdbms;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Driver;
import java.sql.SQLException;

import static org.jetbrains.dekaf.jdbc.JdbcTestHelper.PROVIDER;
import static org.junit.jupiter.api.Assertions.*;



@Tag("UnitTest")
public class JdbcProviderTest {


    //@Test public void acceptConnectionString_Oracle_OCI()  { acceptConnectionString(Oracle.RDBMS, "jdbc:oracle:oci:tester/test@testing"); }
    //@Test public void acceptConnectionString_Oracle_Thin() { acceptConnectionString(Oracle.RDBMS, "jdbc:oracle:thin:@//localhost:1521/XE"); }
    //@Test public void acceptConnectionString_Postgres()    { acceptConnectionString(Postgres.RDBMS, "jdbc:postgresql:///"); }
    @Test public void acceptConnectionString_H2_Mem()      { acceptConnectionString(H2db.RDBMS, "jdbc:h2:mem:test"); }


    private void acceptConnectionString(@NotNull Rdbms rdbms, @NotNull String connectionString) {
        boolean ok = PROVIDER.supportedConnectionString(connectionString);
        assertTrue(ok);

        JdbcFacade facade = PROVIDER.createFacade(connectionString);
        assertEquals(rdbms, facade.getRdbms());

        Driver driver = facade.ensureDriver();
        assertNotNull(driver);
        try {
            assertTrue(driver.acceptsURL(connectionString));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Driver driver2 = facade.getSpecificService(Driver.class, ImplementationAccessibleService.Names.JDBC_DRIVER);
        //assertSame(driver, driver2);
    }


}