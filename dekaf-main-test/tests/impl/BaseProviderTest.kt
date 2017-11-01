package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.assertions.IsNotEmpty
import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Test


class BaseProviderTest {

    @Test
    fun hasAtLeastOneInterProvider() {
        val provider = BaseProvider()
        provider.interProviders expected IsNotEmpty
    }

}