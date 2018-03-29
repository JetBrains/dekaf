package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.H2db;
import org.jetbrains.dekaf.core.Settings;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jetbrains.dekaf.jdbc.JdbcTestHelper.queryString;



@Tag("jdbc")
class JdbcFacadeDriverTest extends JdbcDriverTestCase {

    @Test
    public void connectH2_usingFacadeSettings() {
        JdbcFacade facade = new JdbcFacade(null, H2db.RDBMS, new SpecificForH2db());
        facade.setUp(H2mem.settings);
        facade.activate();
    }


    @Test
    public void specifyProperties() {
        Settings newSettings =
            H2mem.settings.override("connection.parameter.MODE", "MySQL",
                                    "connection.parameter.DEFAULT_ESCAPE", "!");

        JdbcFacade facade = new JdbcFacade(null, H2db.RDBMS, new SpecificForH2db());
        facade.setUp(newSettings);
        facade.activate();

        String query1 = "select VALUE from INFORMATION_SCHEMA.SETTINGS where NAME = 'MODE'";
        String query2 = "select VALUE from INFORMATION_SCHEMA.SETTINGS where NAME = 'DEFAULT_ESCAPE'";

        String value1 = queryString(facade, query1);
        String value2 = queryString(facade, query2);

        assertThat(value1).isEqualToIgnoringCase("MySQL");
        assertThat(value2).isEqualToIgnoringCase("!");
    }

}
