package org.jetbrains.dekaf.jdbc;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;



public abstract class JdbcDriverTestCase {


    protected static final String JDBC_DIR_PATH = "./lib/jdbc";

    protected static final String JTDS_JAR_NAME = "jtds-1.2.8.jar";
    protected static final String JTDS_DRIVER_CLASS_NAME = "net.sourceforge.jtds.jdbc.Driver";

    @NotNull
    protected static Path ourJdbcDir = Paths.get(JDBC_DIR_PATH);


    @BeforeAll
    static void locateJdbcDir() throws IOException {
        Path currentDir = Paths.get(".").toRealPath();
        if (Files.exists(currentDir.resolve("tests"))) {
            ourJdbcDir = currentDir.getParent().resolve(JDBC_DIR_PATH);
        }

        assertThat(Files.exists(ourJdbcDir))
            .withFailMessage("Path %s should exist", ourJdbcDir)
            .isTrue();
        assertThat(Files.isDirectory(ourJdbcDir))
            .withFailMessage("Path %s should be a directory with JDBC drivers")
            .isTrue();

        Path driverFile = ourJdbcDir.resolve(JTDS_JAR_NAME);
        assertThat(Files.exists(driverFile))
            .withFailMessage("Jar %s should exist", driverFile)
            .isTrue();
        assertThat(Files.isRegularFile(driverFile))
            .withFailMessage("Jar %s should exist", driverFile)
            .isTrue();
    }


}
