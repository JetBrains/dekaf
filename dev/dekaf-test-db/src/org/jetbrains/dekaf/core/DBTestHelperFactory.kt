package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.Rdbms


/**
 * Creates applicable test helpers.
 *
 * @author Leonid Bushuev
 */
interface DBTestHelperFactory {

    /**
     * RDBMS that this factory supports.
     * 
     * @return the set of supported RDBMS.
     */
    fun supportRdbms(): Set<Rdbms>


    /**
     * Creates a helper for the specified RDBMS.
     *
     * This method creates a separate instance on each call.
     *
     * @param facade the facade of database to create a helper for.
     * @return       just created helper.
     */
    fun createTestHelperFor(facade: DBFacade): DBTestHelper

}
