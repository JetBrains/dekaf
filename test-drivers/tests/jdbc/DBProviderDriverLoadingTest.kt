package org.jetbrains.dekaf.jdbc

import org.jetbrains.dekaf.H2db
import org.jetbrains.dekaf.Rdbms
import org.jetbrains.dekaf.assertions.IsNotNull
import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.assertions.expectedGreaterThan
import org.jetbrains.dekaf.assertions.expectedSameAs
import org.jetbrains.dekaf.core.DBFacade
import org.jetbrains.dekaf.core.DBProvider
import org.jetbrains.dekaf.impl.BaseProvider
import org.jetbrains.dekaf.util.Version
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

/**
 *
 */
@Tag("DriverTest")
class DBProviderDriverLoadingTest {


    @Test fun loadDriver_H2()       = loadDriver(H2db.RDBMS)
    @Test fun loadDriver_H2_again() = loadDriver(H2db.RDBMS)


    private fun loadDriver(rdbms: Rdbms) {

        val provider: DBProvider = BaseProvider()
        val facade: DBFacade = provider.provide(rdbms)

        facade.rdbms() expectedSameAs rdbms

        facade.activateDriver()
        val driverInfo = facade.driverInfo

        driverInfo expected IsNotNull
        driverInfo!!

        driverInfo.driverName.length expectedGreaterThan 0
        driverInfo.driverVersion     expectedGreaterThan Version.ZERO

    }


}