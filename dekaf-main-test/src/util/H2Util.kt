package org.jetbrains.dekaf.mainTest.util

import org.jetbrains.dekaf.inter.settings.Settings


val H2memSettings =
        Settings.of(
                "driver", Settings.of("class", "org.h2.Driver"),
                "jdbc", Settings.of("connection-string", "jdbc:h2:mem:test")
        )
