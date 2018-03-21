package org.jetbrains.dekaf.expectation


/**
 * Specifies the aspect of testing.
 * When a checking is failed, the given message be presented in the exception.
 */
fun TextMatter.theAspect(aspect: String, body: (TextMatter.() -> Unit)): TextMatter {
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
fun TextMatter.expecting(expect: String) =
        copy(expect = expect)


val TextMatter.beNotNull: TextMatter
    get() = if (something == null) blame(expect = "Non-null value of type $declaredType")
    else this


fun TextMatter.satisfy(predicate: (x: String) -> Boolean): TextMatter =
        if (predicate(text)) this
        else blame(expect = "satisfying the specified predicate")

fun TextMatter.satisfy(expect: String, predicate: (x: String) -> Boolean): TextMatter =
        if (predicate(text)) this
        else blame(expect = expect)

