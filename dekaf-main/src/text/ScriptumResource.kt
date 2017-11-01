package org.jetbrains.dekaf.text

import org.jetbrains.dekaf.util.collectionToString
import java.util.*


/**
 * Represents one resource of SQL texts.
 *
 * @author Leonid Bushuev
 */
abstract class ScriptumResource {


    ////// STATE \\\\\\

    /**
     * All fragments.
     *
     *
     * Null means not loaded yet.
     *
     */
    protected var myFragments: Array<TextFileFragment>? = null

    /**
     * Map of name to fragment.

     *
     *
     * Null means not loaded yet.
     *
     */
    protected var myFragmentsMap: MutableMap<String, TextFileFragment>? = null


    ////// INITIALIZATION \\\\\\

    protected abstract fun loadFragments(): Array<TextFileFragment>


    protected fun loadIfNeeded() {
        if (myFragments == null) {
            myFragments = loadFragments()

            val map = TreeMap<String, TextFileFragment>(String.CASE_INSENSITIVE_ORDER)
            for (fragment in myFragments!!) {
                if (fragment.fragmentName != null) {
                    map.put(fragment.fragmentName, fragment)
                }
            }
            myFragmentsMap = map
        }
    }


    ////// IMPLEMENTATION \\\\\\


    operator fun get(name: String): TextFileFragment {
        val fragment = find(name) ?: if (myFragmentsMap == null) {
            throw IllegalStateException("The scriptum resource is not loaded yet.")
        }
        else {
            val msg = "No such fragment with name: " + name + '\n' +
                    collectionToString(myFragmentsMap!!.keys,
                                       ", ",
                                       "There are fragments: ",
                                       ".",
                                       "This resource is empty")
            throw IllegalArgumentException(msg)
        }

        return fragment
    }


    fun find(name: String): TextFileFragment? {
        loadIfNeeded()
        return myFragmentsMap!![name]
    }


    val existentNames: Array<String?>
        get() {
            loadIfNeeded()
            val fragments = myFragments!!
            return Array(fragments.size) { i -> fragments[i].fragmentName }
        }


    fun count(): Int {
        loadIfNeeded()
        return myFragments!!.size
    }

}
