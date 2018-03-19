package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.util.*

@Tag("ExpectationDemoTest")
class DemoFailCollectionTest {


    @Test
    fun nullContainsElements() {
        val set: Set<String>? = null
        set.must.contain("X", "Y")
    }

    @Test
    fun emptyContainsElements() {
        val set: Set<String> = emptySet()
        set.must.contain("X", "Y")
    }

    @Test
    fun containsElements() {
        val set = setOf("A", "B", "C", "D", "E", "F")
        set.must.contain("B", "F", "G")
    }

    @Test
    fun containsSet() {
        val set = setOf("A", "B", "C", "D", "E", "F")
        set.must.contain(setOf("B", "F", "G"))
    }

    @Test
    fun containsExactlyDifferentSize() {
        val list = listOf("Dog", "Cat", "Rat", "Mouse")
        list.must.containExactly("Dog", "Cat", "Mouse")
    }

    @Test
    fun containsExactlyDifferentElements() {
        val list = listOf("Dog", "Cat", "Rat", "GuineaPig")
        list.must.containExactly("Dog", "Cat", "Mouse", "GuineaPig")
    }

    @Test
    fun containsExactlyDifferentOrder1() {
        val list = listOf("Dog", "Cat", "Rat", "GuineaPig")
        list.must.containExactly("Dog", "Mouse", "GuineaPig", "Cat")
    }

    @Test
    fun containsExactlyDifferentOrder2() {
        val set = TreeSet<Int>()
        set.addAll(arrayOf(111,222,-1,333,444))
        set.must.containExactly(111,222,-1,333,444)
    }

    @Test
    fun notContainsElements() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.notContain("F", "G", "H")
    }

    @Test
    fun notContainsSet() {
        val set = setOf("A", "B", "C", "D", "E", "F", "G")
        set.must.notContain(setOf("F", "G", "H"))
    }

    @Test
    fun notContainsList() {
        val list = setOf("A", "B", "C", "D", "E", "F", "G")
        list.must.notContain(listOf("F", "G", "H"))
    }

    @Test
    fun satisfy1() {
        val set = setOf(21, 31, 45, 50, 77, 210)
        set.must.everyElementSatisfy { it % 3 == 0 || it % 7 == 0 }
    }

    @Test
    fun satisfy2() {
        val set = setOf(21, 31, 45, 50, 77, 210)
        set.must.everyElementSatisfy("divide by 3 or by 7") { it % 3 == 0 || it % 7 == 0 }
    }

}