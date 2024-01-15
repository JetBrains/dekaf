package org.jetbrains.dekaf.jdbc.test

import lb.yaka.base.expectations.iz
import lb.yaka.base.gears.expect
import org.jetbrains.dekaf.inter.test.utils.UnitTest
import org.jetbrains.dekaf.jdbc.drivers.JdbcDriverLoader
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class JdbcDirectoryTest : UnitTest {

    @Test @Order(1)
    fun checkCurrentDirectory() {
        val driversDir: Path = driversDir()
        expect that driversDir.isDirectory() aka "The 'jdbc-drivers' folder existence" iz true
    }

    private fun driversDir(): Path {
        val currentDir: Path = JdbcDriverLoader.getCurrentDir()
        val driversDir: Path = currentDir.resolve("jdbc-drivers")
        return driversDir
    }

    @Test @Order(2)
    fun checkDownloadedDrivers_forH2() {
        val driversDir: Path = driversDir()
        val jarForH2 = driversDir.resolve("h2").resolve(DriverFiles.jarNameForH2)
        expect that jarForH2.isRegularFile() aka "H2 driver jar" iz true
    }

    @Test @Order(3)
    fun checkDownloadedDrivers_forPostgres() {
        val driversDir: Path = driversDir()
        val jarForPostgres = driversDir.resolve("postgres").resolve(DriverFiles.jarNameForPostgres)
        expect that jarForPostgres.isRegularFile() aka "Postgres driver jar" iz true
    }

}