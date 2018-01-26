package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.DekafMaster
import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test


@Tags(Tag("Common"), Tag("UnitTest"))
class CommonConnectionTest {


    @Test
    fun basicConnectDisconnect() {
        val facade = DekafMaster.provider.provide(ConnectionData.connectionString)

        facade.isConnected expected false

        facade.connect()

        facade.isConnected expected true

        val info = facade.connectionInfo
        info.driverVersion.isOrGreater(0,0,0,1) expected true

        facade.disconnect()

        facade.isConnected expected false
    }


}