package org.jetbrains.dekaf.expectation


////// ANY \\\\\\


fun <T:Any> Matter<T>.be(expect: T) =
        if (this.thing == expect) { /* OK */ }
        else blame(expect = expect.displayString(), diff = true)


fun <T:Any> Matter<T>.beSameAs(expect: T) =
        when {
            this.thing === expect -> { /* OK */ }
            this.thing == expect  -> blame(expect = expect.objectReference,
                                           actual = thing.objectReference,
                                           details = "Value: " + thing.displayString(),
                                           aspect = "Same instance with: values are similar but are different instances",
                                           diff = false)
            else                  -> blame(expect = expect.displayString(),
                                           aspect = "Same instance with: values are different",
                                           diff = true)
        }


fun <T:Any> Matter<T>.beIn(vararg items: T) =
        if (thing in items) this
        else blame (expect = "One of the following ${items.size} items: \n\t" +
                                   items.joinToString("\n\t", transform = Any::displayString))

fun <T:Any> Matter<T>.beIn(items: Collection<T>) =
        if (thing in items) this
        else blame (expect = "One of the following ${items.size} items: \n\t" +
                                   items.joinToString("\n\t", transform = Any::displayString))




////// COMPARABLE \\\\\\

fun <T:Comparable<T>> Matter<T>.beBetween(range: ClosedRange<T>): Matter<T> =
        beBetween(range.start, range.endInclusive)

fun <T:Comparable<T>> Matter<T>.beBetween(min: T, max: T): Matter<T> {
    with(expect = "Value must be between $min and $max (type: $declaredType)") {
        val x = thing
        val d1 = x.compareTo(min)
        if (d1 < 0) blame (actual = "$displayText (value is too small)")
        val d2 = x.compareTo(max)
        if (d2 > 0) blame (actual = "$displayText (value is too large)")
    }
    return this
}





