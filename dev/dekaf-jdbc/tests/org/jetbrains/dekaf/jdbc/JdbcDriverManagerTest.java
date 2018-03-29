package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.core.DekafSettingNames;
import org.jetbrains.dekaf.core.Settings;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Driver;

import static org.assertj.core.api.Assertions.assertThat;



@Tag("jdbc")
public class JdbcDriverManagerTest extends JdbcDriverTestCase {

    @Test
    public void getH2() {
        Settings settings = new Settings(DekafSettingNames.DriverClassName, "org.h2.Driver");
        JdbcDriverManager manager = new JdbcDriverManager();
        JdbcDriverBox box = manager.loadDriver(settings);
        Driver h2 = box.driver;
        assertThat(h2).isNotNull();
    }

}
