package org.jetbrains.dekaf.core


/**
 * A session with database.
 *
 *
 * The instance of this session can be borrowed from [DBFacade].
 *
 * @see DBFacade
 * @see DBTransaction
 */
interface DBSession : DBTransactionAware, DBTransaction, ImplementationAccessibleService {


}
