package org.jetbrains.dekaf.configurations

import org.jetbrains.dekaf.core.Settings


/**
 * Database connection configuration.
 */
data class ConnectConfiguration
(
        /**
         * Name of the configuration.
         *
         * When configurations are loaded from a special configurations file,
         * this name is a name from the section.
         */
        @JvmField
        val name: String,

        /**
         * Inherited configurations.
         */
        @JvmField
        val inherited: Collection<ConnectConfiguration>,

        /**
         * The own settings — settings that were read from the configuration file.
         */
        @JvmField
        val ownSettings: Settings,

        /**
         * The effect settings — settings that were read and inherited.
         */
        @JvmField
        val effectSettings: Settings
)
{

    /**
     * The effected setting by name.
     */
    operator fun get(name: String): String? = effectSettings.get(name)

    /**
     * The effected setting by name, or the given default value if not.
     */
    operator fun get(name: String, defaultValue: String): String = effectSettings.get(name, defaultValue)
}