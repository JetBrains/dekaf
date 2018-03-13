package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.H2db;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;



@Tag("JdbcDriverTest")
class JdbcFacadeDriverTest extends JdbcDriverTestCase {

    @Test
    public void connectH2_usingFacadeSettings() {
        JdbcFacade facade = new JdbcFacade(null, H2db.RDBMS, new SpecificForH2db());
        facade.setUp(H2mem.settings);
        facade.activate();
    }

}
