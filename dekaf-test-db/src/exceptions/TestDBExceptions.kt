package org.jetbrains.dekaf.exceptions


class DBProtectionException(message: String, statementText: String?)
    : DBException(message, statementText)