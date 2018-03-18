package org.jetbrains.dekaf.expectation


/**
 * Specifies the aspect of testing.
 * When a checking is failed, the given message be presented in the exception.
 */
fun<T:Any> Matter<T>.theAspect(aspect: String, body: (Matter<T>.() -> Unit)): Matter<T> {
    val newMatter = copy(aspect = if (this.aspect == null) aspect else this.aspect + ": " + aspect)
    newMatter.body()
    return this
}

/**
 * Overrides the expectation text.
 * When a checking failed, this text will be presented in the exception instead of normal expectation text.
 *
 * This function is designed to be used inside checkers, to specify the expectation text once
 * for several consequent inner checks.
 */
fun<T:Any> Matter<T>.expecting(expect: String) =
        copy(expect = expect)


val <T:Any> Matter<T>.beNotNull: Matter<T>
    get() = if (something == null) blame(expect = "Non-null value of type $declaredType")
    else this


fun <T:Any> Matter<T>.satisfy(predicate: (x: T) -> Boolean): Matter<T> =
        if (predicate(thing)) this
        else blame(expect = "satisfying the specified predicate")

fun <T:Any> Matter<T>.satisfy(expect: String, predicate: (x: T) -> Boolean): Matter<T> =
        if (predicate(thing)) this
        else blame(expect = expect)

