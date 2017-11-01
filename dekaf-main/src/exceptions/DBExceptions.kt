package org.jetbrains.dekaf.exceptions


open class DBServiceIsNotActiveException(message: String) : DBException(message, null)

class DBSessionIsClosedException(message: String) : DBServiceIsNotActiveException(message)

class DBTransactionIsAlreadyStartedException : DBException {

    constructor() : super("Transaction is already started.", null) {}

    constructor(message: String) : super(message, null) {}

}
