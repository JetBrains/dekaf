package org.jetbrains.dekaf.main.db


interface DbQueryRunner<T>: AutoCloseable {

    fun run(vararg paramValues: Any?): T

}
