package org.jetbrains.dekaf.jdbcTest.impl

import lb.yaka.expectations.iz
import lb.yaka.gears.expect
import lb.yaka.gears.notEmpty
import lb.yaka.utils.isInstanceOf
import org.jetbrains.dekaf.inter.intf.InterServiceFactory
import org.jetbrains.dekaf.jdbc.impl.JdbcServiceFactory
import org.jetbrains.dekaf.test.utils.UnitTest
import org.junit.jupiter.api.Test
import java.util.*
import java.util.stream.Collectors


class JdbcServiceFactoryTest : UnitTest {

    @Test
    fun serviceLoading() {
        val loader: ServiceLoader<InterServiceFactory> =
                ServiceLoader.load(InterServiceFactory::class.java)
        val list: List<ServiceLoader.Provider<InterServiceFactory>> =
                loader.stream().collect(Collectors.toList())

        expect that list iz notEmpty

        val factory = list.first().get()

        expect that factory isInstanceOf JdbcServiceFactory::class
    }

}