package org.jetbrains.dekaf.expectation


////// ANY \\\\\\


fun <T:Any> Matter<T>.be(expect: T) =
        if (this.thing == expect) { /* OK */ }
        else blame(null, expect = expect.displayString(), diff = true)


fun <T:Any> Matter<T>.beSameAs(expect: T) =
        when {
            this.thing === expect -> { /* OK */ }
            this.thing == expect  -> blame(check = "Same instance check: values are similar but are different instances",
                                           expect = expect.objectReference,
                                           actual = thing.objectReference,
                                           details = "Value: " + thing.displayString(),
                                           diff = false)
            else                  -> blame(check = "Same instance check: values are different",
                                           expect = expect.displayString(),
                                           diff = true)
        }


fun <T:Any> Matter<T>.beIn(vararg items: T) =
        if (thing in items) this
        else blame (null, expect = "One of the following ${items.size} items: \n\t" +
                                   items.joinToString("\n\t", transform = Any::displayString))

fun <T:Any> Matter<T>.beIn(items: Collection<T>) =
        if (thing in items) this
        else blame (null, expect = "One of the following ${items.size} items: \n\t" +
                                   items.joinToString("\n\t", transform = Any::displayString))


fun <T:Any> Matter<T>.satisfy(predicate: (x: T) -> Boolean): Matter<T> =
        if (predicate(thing)) this
        else blame("Predicate satisfaction", expect = "satisfying the specified predicate")


fun <T:Any> Matter<T>.satisfy(check: String, predicate: (x: T) -> Boolean): Matter<T> =
        if (predicate(thing)) this
        else blame(check, expect = "satisfying the specified predicate")



////// COMPARABLE \\\\\\

fun <T:Comparable<T>> Matter<T>.beBetween(min: T, max: T): Matter<T> {
    val d1 = thing.compareTo(min)
    if (d1 < 0) blame (check = "Value must be between $min and $max", actual = "$displayString (value is too small)")
    val d2 = thing.compareTo(max)
    if (d2 > 0) blame (check = "Value must be between $min and $max", actual = "$displayString (value is too large)")
    return this
}





