package org.jetbrains.dekaf.mainTest.base

import lb.yaka.expectations.equalsTo
import lb.yaka.gears.expect
import org.jetbrains.dekaf.inter.settings.Settings
import org.jetbrains.dekaf.main.base.FactoryLoader
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.Test

class FactoryLoaderTest : UnitTest {

    @Test
    fun interFactory_exists() {
        val f = FactoryLoader.selectInterServiceFactory(Settings.empty)
        expect that f.javaClass.name equalsTo "org.jetbrains.dekaf.jdbc.impl.JdbcServiceFactory"
    }

}