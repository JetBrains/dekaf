package org.jetbrains.dekaf.jdbc

import org.jetbrains.dekaf.H2db


internal object H2mem {

    val provider = JdbcProvider()
    val h2facade = JdbcFacade(provider, H2db.RDBMS, SpecificForH2db())

    init {
        h2facade.setConnectionString("jdbc:h2:mem:TestDatabase")
    }

}