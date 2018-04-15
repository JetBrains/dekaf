package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*

@Tag("demo")
class DemoSuccessCollectionTest {


    @Test
    fun basicCollection() {
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
    fun basicArray() {
        val array: Array<CharSequence> = arrayOf(
                StringBuilder().append("6 chars").toString(),
                "Literal",
                ""
        )

        array.must
                .beNotNull
                .hasSize(3)
                .beInstanceOf<Array<*>>()
    }

    @Test
    fun basicLongArray() {
        val array: LongArray = longArrayOf(111L, 222L, 333L, 444L)

        array.must
                .beNotNull
                .hasSize(4)
                .beInstanceOf<LongArray>()
    }

    @Test
    fun `array must be empty`() {
        val array = emptySet<Any>()
        array.must
                .beEmptyOrNull
                .beEmpty
                .hasSize(0)
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
    fun `array must contain given elements`() {
        val array = arrayOf("A", "B", "C", "D", "E", "F", "G")
        array.must.contain("B", "F", "G")
    }

    @Test
    fun `set must contain given elements`() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.contain("B", "F", "G")
    }

    @Test
    fun `array must contain elements specified by a set`() {
        val array = arrayOf("A", "B", "C", "D", "E", "F", "G")
        array.must.contain(setOf("B", "F", "G"))
    }

    @Test
    fun `set must contain elements specified by another set`() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.contain(setOf("B", "F", "G"))
    }

    @Test
    fun `array must contain exactly given elements in the proper order`() {
        val array = arrayOf(125, 941, 374, -2)
        array.must.containExactly(125, 941, 374, -2)
    }

    @Test
    fun `list must contain exactly given elements in the proper order`() {
        val list = listOf(125, 941, 374, -2)
        list.must.containExactly(125, 941, 374, -2)
    }

    @Test
    fun `sorted set must contain exactly given elements in the proper order`() {
        val set = TreeSet<Int>()
        set.addAll(Arrays.asList(111, 222, 333, 444))
        set.must.containExactly(111, 222, 333, 444)
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