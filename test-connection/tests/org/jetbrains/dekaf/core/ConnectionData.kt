@file:Suppress("JoinDeclarationAndAssignment")

package org.jetbrains.dekaf.core

import org.jetbrains.dekaf.TestEnvironment


object ConnectionData {

    val connectionString: String


    init {
        connectionString = TestEnvironment.obtainConnectionString()
    }

}