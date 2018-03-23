package org.jetbrains.dekaf.expectation


/**
 * Specifies the aspect of testing.
 * When a checking is failed, the given message be presented in the exception.
 */
fun<E:Any,T:Any> MultiMatter<E,T>.theAspect(aspect: String): MultiMatter<E,T> =
        copy(aspect = if (this.aspect == null) aspect else this.aspect + ": " + aspect)

/**
 * Overrides the expectation text.
 * When a checking failed, this text will be presented in the exception instead of normal expectation text.
 *
 * This function is designed to be used inside checkers, to specify the expectation text once
 * for several consequent inner checks.
 */
fun<E:Any,T:Any> MultiMatter<E,T>.expecting(expect: String): MultiMatter<E,T> =
        copy(expect = expect)


val <E:Any,T:Any> MultiMatter<E,T>.beNotNull: MultiMatter<E,T>
    get() = if (something == null) blame(expect = "Non-null value of type $declaredType")
    else this


fun <E:Any,T:Any> MultiMatter<E,T>.satisfy(predicate: (x: T) -> Boolean): MultiMatter<E,T> =
        if (predicate(thing)) this
        else blame(expect = "satisfying the specified predicate")

fun <E:Any,T:Any> MultiMatter<E,T>.satisfy(expect: String, predicate: (x: T) -> Boolean): MultiMatter<E,T> =
        if (predicate(thing)) this
        else blame(expect = expect)

