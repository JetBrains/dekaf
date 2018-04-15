package org.jetbrains.dekaf.configurations

import org.jetbrains.dekaf.util.optimizeToList
import org.jetbrains.dekaf.util.optimizeToMap

/**
 * Database connection configurations.
 */
class ConnectConfigurations
{
    /**
     * Sections.
     */
    @JvmField
    val sections: List<ConnectConfiguration>

    private val names: Map<String, ConnectConfiguration>


    constructor(sections: Collection<ConnectConfiguration>) {
        this.sections = sections.optimizeToList()
        this.names = sections.optimizeToMap { it.name to it }
    }


    internal constructor(sections: List<ConnectConfiguration>, names: Map<String, ConnectConfiguration>) {
        this.sections = sections
        this.names = names
    }


}