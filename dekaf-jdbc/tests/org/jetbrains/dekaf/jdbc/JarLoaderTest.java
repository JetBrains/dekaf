package org.jetbrains.dekaf.jdbc;

import org.jetbrains.dekaf.util.JarLoader;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;



@Tag("jdbc")
public class JarLoaderTest extends JdbcDriverTestCase
{

    @Test
    public void loadJtdsJar() throws ClassNotFoundException {
        JarLoader loader = new JarLoader();
        ClassLoader driverClassLoader = loader.load(ourJdbcDir, new String[] { JTDS_JAR_NAME });
        driverClassLoader.loadClass(JTDS_DRIVER_CLASS_NAME);
    }

}
