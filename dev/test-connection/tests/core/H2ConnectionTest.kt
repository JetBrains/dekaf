package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.DekafMaster
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test


@Tags(Tag("H2"), Tag("UnitTest"))
class H2ConnectionTest {

    @Test
    fun connectMemory() {
        val connectString1 = "jdbc:h2:mem:TestDatabase1"
        val connectString2 = "jdbc:h2:mem:TestDatabase2"
        val facade1 = DekafMaster.provider.provide(connectString1)
        val facade2 = DekafMaster.provider.provide(connectString2)
        facade1.connect()
        facade2.connect()
        facade1.disconnect()
        facade2.disconnect()
    }

}