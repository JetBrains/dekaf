@file:JvmName("Blame")

package org.jetbrains.dekaf.expectation

import org.opentest4j.AssertionFailedError


fun <T:Any> blame(matter: Matter<out T>, expectationText: CharSequence): Nothing {
    val b = StringBuilder()
    b.append("FAIL: \n")
    b.append("ACTUAL: ").append(matter.thing.displayString()).append('\n')
    b.append("EXPECT: ").append(expectationText).append('\n')
    throw BasicAssertionFailedError(b.toString())
}


fun <T:Any> blame(matter: Matter<out T>, description: String, expectationText: CharSequence): Nothing {
    val b = StringBuilder()
    b.append("FAIL:   ").append(description).append('\n')
    b.append("ACTUAL: ").append(matter.thing.displayString()).append('\n')
    b.append("EXPECT: ").append(expectationText).append('\n')
    throw BasicAssertionFailedError(b.toString())
}


fun <T:Any> blameDiff(matter: Matter<out T>, expect: T): Nothing {
    val b = StringBuilder()
    b.append("FAIL:   Values are different\n")
    b.append("ACTUAL: ").append(matter.thing.displayString()).append('\n')
    b.append("EXPECT: ").append(expect.displayString()).append('\n')
    throw DiffAssertionFailedError(b.toString(), expect, matter.thing)
}


fun <T : Any> blameNotSame(matter: Matter<out T>, expect: T): Nothing =
        blameActualExpectValueDiff("Values are similar but not the same",
                                   matter.thing, matter.thing.objectReference,
                                   expect, expect.objectReference,
                                   matter.thing.displayString())

internal fun blameActualExpectValueDiff(description: String,
                                        actualValue: Any,
                                        actualText: String,
                                        expectValue: Any,
                                        expectText: String,
                                        valueText: String): Nothing {
    val b = StringBuilder()
    b.append("FAIL:   ").append(description).append('\n')
    b.append("ACTUAL: ").append(actualText).append('\n')
    b.append("EXPECT: ").append(expectText).append('\n')
    b.append("VALUE:  ").append(valueText).append('\n')
    throw DiffAssertionFailedError(b.toString(), expectValue, actualValue)
}

fun blameActualExpectValue(description: String,
                           actualText: String,
                           expectText: String,
                           valueText: String): Nothing {
    val b = StringBuilder()
    b.append("FAIL:   ").append(description).append('\n')
    b.append("ACTUAL: ").append(actualText).append('\n')
    b.append("EXPECT: ").append(expectText).append('\n')
    b.append("VALUE:  ").append(valueText).append('\n')
    throw BasicAssertionFailedError(b.toString())
}


fun blameNull(matter: NullMatter<*>): Nothing {
    blameNull("an value (of declared type: ${matter.declaredType.simpleName})")
}

fun blameNull(expectationText: CharSequence): Nothing {
    throw BasicAssertionFailedError("FAIL: Actual is NULL when expected: $expectationText")
}


class BasicAssertionFailedError: AssertionFailedError
{
    constructor(message: String) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}


class DiffAssertionFailedError: AssertionFailedError
{
    constructor(message: String?, expected: Any?, actual: Any?) : super(message, expected, actual)
}

