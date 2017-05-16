package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.Rdbms
import org.jetbrains.dekaf.inter.InterProvider


interface DBProvider {

    /**
     * Intermediate providers.
     * The order of providers is important: when Dekaf looks
     * for an appropriate provider, it iterates them in the list order.
     */
    var interProviders: List<InterProvider>

    /**
     * Provides with a DB facade applicable for the specified RDBMS.
     */
    fun provide(rdbms: Rdbms): DBFacade

    /**
     * Provides with a DB facade applicable for the given connection string.
     */
    fun provide(connectionString: String): DBFacade

    /**
     * Provides with a DB facade applicable for the specified RDBMS,
     * using the given intermediate provider.
     */
    fun provide(interProvider: InterProvider, rdbms: Rdbms): DBFacade

    /**
     * Provides with a DB facade applicable for the given connection string,
     * using the given intermediate provider.
     */
    fun provide(interProvider: InterProvider, connectionString: String): DBFacade

}
