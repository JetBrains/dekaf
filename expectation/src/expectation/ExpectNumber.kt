package org.jetbrains.dekaf.expectation

import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.absoluteValue
import kotlin.math.sign


/// NUMBER \\\

fun Matter<Number>.be(expect: Byte) =
        if (thing compareTo expect.toInt() == 0) this
        else blame(expect = expect.toString())

fun Matter<Number>.be(expect: Short) =
        if (thing compareTo expect.toInt() == 0) this
        else blame(expect = expect.toString())

fun Matter<Number>.be(expect: Int) =
        if (thing compareTo expect == 0) this
        else blame(expect = expect.toString())

fun Matter<Number>.be(expect: Long) =
        if (thing compareTo expect == 0) this
        else blame(expect = expect.toString())


fun Matter<Float>.be(expect: Float, tolerance: Float) =
        if (thing >= expect-tolerance && thing <= expect+tolerance) this
        else blame(expect = expect.displayString(), details = "delta is ${(thing-expect).absoluteValue}")

fun Matter<Double>.be(expect: Double, tolerance: Double) =
        if (thing >= expect-tolerance && thing <= expect+tolerance) this
        else blame(expect = expect.displayString(), details = "delta is ${(thing-expect).absoluteValue}")



fun <N:Number> Matter<N>.beBetween(range: IntRange): Matter<N> {
    val min = range.first
    val max = range.last
    with(expect = "Value between $min and $max") {
        val d1 = thing compareTo min
        if (d1 < 0) blame (actual = "$displayText (value is too small)")
        val d2 = thing.compareTo(max)
        if (d2 > 0) blame (actual = "$displayText (value is too large)")
    }
    return this
}

fun <N:Number> Matter<N>.beBetween(range: LongRange): Matter<N> {
    val min = range.first
    val max = range.last
    with(expect = "Value between $min and $max") {
        val d1 = thing compareTo min
        if (d1 < 0) blame (actual = "$displayText (value is too small)")
        val d2 = thing.compareTo(max)
        if (d2 > 0) blame (actual = "$displayText (value is too large)")
    }
    return this
}


fun <N:Number> Matter<N>.beLess(threshold: Int): Matter<N> =
        if (thing compareTo threshold < 0) this
        else blame (expect = "A value less than $threshold")

fun <N:Number> Matter<N>.beLess(threshold: Long): Matter<N> =
        if (thing compareTo threshold < 0) this
        else blame (expect = "A value less than $threshold")

fun <N:Number> Matter<N>.beLessOrEqual(threshold: Int): Matter<N> =
        if (thing compareTo threshold <= 0) this
        else blame (expect = "A value less than or equal to $threshold")

fun <N:Number> Matter<N>.beLessOrEqual(threshold: Long): Matter<N> =
        if (thing compareTo threshold <= 0) this
        else blame (expect = "A value less than or equal to $threshold")

fun <N:Number> Matter<N>.beGreaterOrEqual(threshold: Int): Matter<N> =
        if (thing compareTo threshold >= 0) this
        else blame (expect = "A value greater than or equal to $threshold")

fun <N:Number> Matter<N>.beGreaterOrEqual(threshold: Long): Matter<N> =
        if (thing compareTo threshold >= 0) this
        else blame (expect = "A value greater than or equal to $threshold")

fun <N:Number> Matter<N>.beGreater(threshold: Int): Matter<N> =
        if (thing compareTo threshold > 0) this
        else blame (expect = "A value greater than $threshold")

fun <N:Number> Matter<N>.beGreater(threshold: Long): Matter<N> =
        if (thing compareTo threshold > 0) this
        else blame (expect = "A value greater than $threshold")




val Matter<Number>.beZero
    get() =
        if (thing.signum == 0) this
        else blame(expect = "0")

val Matter<Number>.beNonZero
    get() =
        if (thing.signum != 0) this
        else blame(expect = "not 0")

val Matter<Number>.bePositive
    get() =
        if (thing.signum == +1) this
        else blame(expect = "a positive value")

val Matter<Number>.beNegative
    get() =
        if (thing.signum == -1) this
        else blame(expect = "a negative value")

val Matter<Number>.bePositiveOrZero
    get() =
        if (thing.signum >= 0) this
        else blame(expect = "a positive or zero value")

val Matter<Number>.beNegativeOrZero
    get() =
        if (thing.signum <= 0) this
        else blame(expect = "a negative or zero value")




////// BOOLEAN \\\\\\

val Matter<Boolean>.beTrue
    get() = thing("true").apply { if (!thing) blame(expect = "true") }

val Matter<Boolean>.beFalse
    get() = thing("false").apply { if (thing) blame(expect = "false") }





/// INTERNAL FUNCTIONS \\\

internal val Number.signum: Int
    get() =
        when (this) {
            is Byte          -> if (this > 0) +1 else if (this < 0) -1 else 0
            is Short         -> if (this > 0) +1 else if (this < 0) -1 else 0
            is Int           -> this.sign
            is Long          -> this.sign
            is BigInteger    -> signum()
            is BigDecimal    -> signum()
            is Float         -> if (this > 0) +1 else if (this < 0) -1 else 0
            is Double        -> if (this > 0) +1 else if (this < 0) -1 else 0
            is AtomicInteger -> if (this.get() > 0) +1 else if (this.get() < 0) -1 else 0
            is AtomicLong    -> if (this.get() > 0L) +1 else if (this.get() < 0L) -1 else 0
            else -> throw IllegalArgumentException("Unknown how to get signum of a value of type ${this.javaClass}")
        }


internal infix fun Number.compareTo(value: Int) =
        when (this) {
            is Byte          -> this.toInt().compareTo(value)
            is Short         -> this.toInt().compareTo(value)
            is Int           -> this.compareTo(value)
            is Long          -> this.compareTo(value.toLong())
            is BigInteger    -> this.compareTo(java.math.BigInteger.valueOf(value.toLong()))
            is BigDecimal    -> this.compareTo(java.math.BigDecimal.valueOf(value.toLong()))
            is Float         -> this.compareTo(value)
            is Double        -> this.compareTo(value)
            is AtomicInteger -> this.get().compareTo(value)
            is AtomicLong    -> this.get().compareTo(value.toLong())
            else -> throw IllegalArgumentException("Unknown how to get compare of a value of type ${this.javaClass} with integer")
        }


internal infix fun Number.compareTo(value: Long) =
        when (this) {
            is Byte          -> this.toInt().compareTo(value)
            is Short         -> this.toInt().compareTo(value)
            is Int           -> this.compareTo(value)
            is Long          -> this.compareTo(value)
            is BigInteger    -> this.compareTo(java.math.BigInteger.valueOf(value))
            is BigDecimal    -> this.compareTo(java.math.BigDecimal.valueOf(value))
            is Float         -> this.compareTo(value)
            is Double        -> this.compareTo(value)
            is AtomicInteger -> this.get().compareTo(value)
            is AtomicLong    -> this.get().compareTo(value)
            else -> throw IllegalArgumentException("Unknown how to get compare of a value of type ${this.javaClass} with long integer")
        }


