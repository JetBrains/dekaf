package org.jetbrains.dekaf.impl

import org.jetbrains.dekaf.H2db
import org.jetbrains.dekaf.Rdbms
import org.jetbrains.dekaf.core.DekafSettingNames
import org.jetbrains.dekaf.core.Settings
import org.jetbrains.dekaf.util.matches
import java.util.regex.Pattern


internal class AutoDescriptor
(
    val rdbms: Rdbms,
    val connectionStringPattern: Pattern,
    val defaultSettings: Settings
)
{
    constructor(rdbms: Rdbms, connectionStringPattern: String, vararg settings: Pair<String,String>)
        : this(rdbms, Pattern.compile(connectionStringPattern), Settings(mapOf(*settings)))
}


internal val autoDescriptors =
        listOf(
                AutoDescriptor(H2db.RDBMS, "jdbc:h2:.*",
                               DekafSettingNames.DriverClassName to "org.h2.Driver")
        )


internal fun findDescriptor(connectionString: String): AutoDescriptor? {
    for (descriptor in autoDescriptors) if (connectionString matches descriptor.connectionStringPattern) return descriptor
    return null
}
