package org.jetbrains.dekaf.util

import org.jetbrains.dekaf.assertions.expected
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test


/**
 *
 */
@Tag("UnitTest")
class CollectExtTest {

    @Test
    fun chopBy_basic() {

        listOf(1L, 2L, 3L) chopBy 5 expected listOf(listOf(1L, 2L, 3L))
        listOf(1L, 2L, 3L) chopBy 3 expected listOf(listOf(1L, 2L, 3L))

        listOf(1L, 2L, 3L, 4L, 5L) chopBy 3 expected listOf(listOf(1L, 2L, 3L), listOf(4L, 5L))

    }

    @Test
    fun chopBy_zeroSlice() {
        expectIllegalArgumentException {
            listOf(1L, 2L) chopBy 0
        }
    }

    @Test
    fun chopAndPadBy_basic() {

        listOf(1L, 2L, 3L) chopAndPadBy 5 expected listOf(listOf(1L, 2L, 3L, null, null))
        listOf(1L, 2L, 3L) chopAndPadBy 3 expected listOf(listOf(1L, 2L, 3L))

        listOf(1L, 2L, 3L, 4L, 5L) chopAndPadBy 3 expected listOf(listOf(1L, 2L, 3L), listOf(4L, 5L, null))

    }

    @Test
    fun chopAndPadBy_zeroSlice() {
        expectIllegalArgumentException {
            listOf(1L, 2L) chopAndPadBy 0
        }
    }



}