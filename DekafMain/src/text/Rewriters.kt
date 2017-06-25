package org.jetbrains.dekaf.text

import java.util.function.Function


/**
 * @author Leonid Bushuev
 */
object Rewriters {

    fun replace(what: String, with: String): Function<String, String> {
        return Function { arg ->
            arg.replace(what, with, false)   // TODO don't use StringsKt.replace here - it violates the rule
        }
    }

    fun replace(map: Map<String, String>): Function<String, String> {
        return Function { arg ->
            if (map.isEmpty()) {
                arg
            }
            else {
                var was = false
                val b = StringBuilder(arg)
                for ((what, with) in map) {
                    var p = b.indexOf(what)
                    while (p >= 0) {
                        b.replace(p, p + what.length, with)
                        p = b.indexOf(what, p + with.length)
                        was = true
                    }
                }
                if (was) b.toString() else arg
            }
        }
    }

}
