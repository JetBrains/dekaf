package org.jetbrains.dekaf.expectation


////// ANY \\\\\\


val <T:Any> OptionalMatter<T>.beNull: Unit
    get() {
        when (this) {
            is NullMatter -> { /* OK */ }
            is Matter -> blame(this, "the null (declared type: ${this.declaredType})")
        }
    }


val <T:Any> OptionalMatter<T>.beNotNull: Matter<T>
    get() = when (this) {
        is NullMatter -> blameNull(this)
        is Matter -> blame(this, "a non-null value (declared type: ${this.declaredType})")
    }



fun <T:Any> OptionalMatter<T>.be(expect: T) =
        m(expect.displayString()).be(expect)

fun <T:Any> Matter<T>.be(expect: T) =
        if (this.thing == expect) { /* OK */ }
        else blameDiff(this, expect)


fun <T:Any> OptionalMatter<T>.beSameAs(expect: T) =
        m("the same instance as the expected value").beSameAs(expect)

fun <T:Any> Matter<T>.beSameAs(expect: T) =
        when {
            this.thing === expect -> { /* OK */ }
            this.thing == expect  -> blameNotSame(this, expect)
            else                  -> blameDiff(this, expect)
        }


fun <T:Any> OptionalMatter<T>.beIn(vararg items: T) =
        m("one of given ${items.size} items")
                .apply {
                    for (item in items) if (thing == item) return@apply
                    val expectationText =
                            "One of the following ${items.size} items: \n\t" +
                                    items.joinToString("\n\t", transform = Any::displayString)
                    blame(this, expectationText)
                }

fun <T:Any> OptionalMatter<T>.beIn(items: Collection<out T>) =
        m("one of given ${items.size} items")
                .apply {
                    for (item in items) if (thing == item) return@apply
                    val expectationText =
                            "One of the following ${items.size} items:\n\t" +
                                    items.joinToString("\n\t", transform = Any::displayString)
                    blame(this, expectationText)
                }



fun <T:Any> OptionalMatter<T>.satisfy(predicate: (x: T) -> Boolean): Matter<T> =
        m("satisfy the specified predicate")
                .apply {
                    if (!predicate(thing)) blame(this, "satisfying the specified predicate")
                }


fun <T:Any> OptionalMatter<T>.satisfy(description: String, predicate: (x: T) -> Boolean): Matter<T> =
        m("satisfy the predicate: $description")
                .apply {
                    if (!predicate(thing)) blame(this, "satisfying the predicate: $description")
                }



////// COMPARABLE \\\\\\

fun <T:Comparable<T>> OptionalMatter<T>.beBetween(min: T, max: T): Matter<T> =
        m("value between ${min.displayString()} and ${max.displayString()} both inclusive")
                .apply {
                    val d1 = thing.compareTo(min)
                    if (d1 < 0) blame(this, "value is too small", "value between ${min.displayString()} and ${max.displayString()} both inclusive")
                    val d2 = thing.compareTo(max)
                    if (d2 > 0) blame(this, "value is too large", "value between ${min.displayString()} and ${max.displayString()} both inclusive")
                }




////// BOOLEAN \\\\\\



val OptionalMatter<Boolean>.beTrue
    get() = m("true").apply { if (!thing) blame(this, "true") }

val OptionalMatter<Boolean>.beFalse
    get() = m("false").apply { if (thing) blame(this, "false") }



