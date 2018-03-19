package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.H2db;
import org.jetbrains.dekaf.core.DekafSettingNames;
import org.jetbrains.dekaf.core.Settings;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("JdbcDriverTest")
public class JdbcProviderTest extends JdbcDriverTestCase
{

    @Test
    public void basicH2_usingSettingsOnProvide() {
        Settings settings = new Settings(DekafSettingNames.DriverClassName, "org.h2.Driver");

        JdbcProvider provider = new JdbcProvider();
        JdbcFacade facade = provider.createFacade(H2db.RDBMS);
    }


}