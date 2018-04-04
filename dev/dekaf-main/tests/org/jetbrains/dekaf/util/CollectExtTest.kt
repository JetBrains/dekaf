package org.jetbrains.dekaf.util

import org.jetbrains.dekaf.assertions.expected
import org.jetbrains.dekaf.expectation.be
import org.jetbrains.dekaf.expectation.containExactly
import org.jetbrains.dekaf.expectation.must
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


/**
 *
 */
@Tag("basic")
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


    @Test
    fun sortTopologicallyBySetDeferringDependents_nothingToChange() {
        val orig = listOf(1L,2L,3L,4L,5L)
        val result = orig.sortTopologicallyBySetDeferringDependents { (1L..it-1).toSet() }

        result.must.containExactly(orig)
    }

    @Test
    fun sortTopologicallyBySetDeferringDependents_basic() {
        val orig = listOf(3L,5L,7L,2L,6L,1L,4L)
        val result = orig.sortTopologicallyBySetDeferringDependents { (1L..it-1).toSet() }

        result.must.containExactly(1L,2L,3L,4L,5L,6L,7L)
    }

    @Test
    fun sortTopologicallyBySetDeferringDependents_reverse() {
        val orig = listOf(7L,6L,5L,4L,3L,2L,1L)
        val result = orig.sortTopologicallyBySetDeferringDependents { (1L..it-1).toSet() }

        result.must.containExactly(1L,2L,3L,4L,5L,6L,7L)
    }

    @Test
    fun sortTopologicallyBySetDeferringDependents_deferDependents() {
        val orig = listOf("X1","Z","X")
        val result = orig.sortTopologicallyBySetDeferringDependents { if (it == "X1") setOf("X") else emptySet() }

        result.must.containExactly("Z","X","X1")
    }

    @Test
    fun sortTopologicallyBySetDeferringDependents_cyclic() {
        val orig = listOf(1L,2L,3L,4L)

        assertThrows<CyclicDependenciesException> {
            orig.sortTopologicallyBySetDeferringDependents { if (it == 1L) setOf(4L) else setOf(it-1) }
        }
    }

    @Test
    fun sortTopologicallyBySetDeferringDependents_alien() {
        val orig = listOf(1L,2L,3L,4L)

        val e = assertThrows<UnexistentDependencyException> {
            orig.sortTopologicallyBySetDeferringDependents { if (it == 3L) setOf(99L) else emptySet() }
        }

        e.element.must.be(99L)
    }


}