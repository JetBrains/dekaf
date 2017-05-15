package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.Rdbms


interface DBProvider {

    fun provide(rdbms: Rdbms): DBFacade

    fun provide(connectionString: String): DBFacade

}
