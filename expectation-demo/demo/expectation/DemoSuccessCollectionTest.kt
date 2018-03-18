package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ExpectationDemoTest")
class DemoSuccessCollectionTest {


    @Test
    fun basic() {
        val collection: Collection<CharSequence> = setOf(
                StringBuilder().append("6 chars").toString(),
                "Literal",
                ""
        )

        collection.must
                .beNotNull
                .hasSize(3)
                .beInstanceOf<Set<*>>()
    }

    @Test
    fun `set must be empty`() {
        val set = emptySet<Any>()
        set.must
                .beEmptyOrNull
                .beEmpty
                .hasSize(0)
    }

    @Test
    fun `set must contain given elements`() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.contain("B", "F", "G")
    }

    @Test
    fun `set must contain elements specified by another set`() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.contain(setOf("B", "F", "G"))
    }

    @Test
    fun `list must contain exactly given elements in the proper order`() {
        val set = listOf(125, 941, 374, -2)
        set.must.containExactly(125, 941, 374, -2)
    }

    @Test
    fun `set must not contain given elements`() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.notContain("X", "Y", "Z")
    }

    @Test
    fun `set must not contain elements specified by a set`() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.notContain(setOf("X", "Y", "Z"))
    }

    @Test
    fun `set must not contain elements specified by a list`() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.notContain(listOf("X", "Y", "Z"))
    }

    @Test
    fun `every set element must satisfy the predicate`() {
        val set = setOf(21, 45, 77, 210)
        set.must.everyElementSatisfy { it % 3 == 0 || it % 7 == 0 }
    }

    @Test
    fun `every set element must satisfy the predicate (with predicate description)`() {
        val set = setOf(21, 45, 77, 210)
        set.must.everyElementSatisfy("divide by 3 or by 7") { it % 3 == 0 || it % 7 == 0 }
    }

}