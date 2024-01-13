package org.jetbrains.dekaf.inter.test.utils

import lb.yaka.base.expectations.containsExactly
import lb.yaka.base.expectations.equalsTo
import lb.yaka.base.expectations.iz
import lb.yaka.base.expectations.sameAs
import lb.yaka.base.gears.empty
import lb.yaka.base.gears.expect
import org.jetbrains.dekaf.inter.utils.SimpleStringConvert.escapeJavaString
import org.jetbrains.dekaf.inter.utils.SimpleStringConvert.importStringList
import org.junit.jupiter.api.Test


class SimpleStringConvertTest : UnitTest {

    @Test
    fun escapeJavaString_basic() {
        expect that escapeJavaString("x\ny\nz") equalsTo """x\ny\nz"""
        expect that escapeJavaString("x\ny\nz") equalsTo """x\ny\nz"""
        expect that escapeJavaString("x\r\ny\r\nz") equalsTo """x\r\ny\r\nz"""
        expect that escapeJavaString("x\ty\bz") equalsTo """x\ty\bz"""
    }

    @Test
    fun escapeJavaString_dontChangeGoodString() {
        val goodString = "Just a good string"
        expect that escapeJavaString(goodString) sameAs goodString
    }


    @Test
    fun importStringList_CSV_basic() {
        val text = "einz,zwei,drei,vier"
        val list = importStringList(text)
        expect that list containsExactly listOf("einz", "zwei", "drei", "vier")
    }

    @Test
    fun importStringList_CSV_trim() {
        val text = "  einz , \t zwei , drei , vier  "
        val list = importStringList(text)
        expect that list containsExactly listOf("einz", "zwei", "drei", "vier")
    }

    @Test
    fun importStringList_lines_basic() {
        val text = "einz\nzwei\ndrei\nvier\n"
        val list = importStringList(text)
        expect that list containsExactly listOf("einz", "zwei", "drei", "vier")
    }

    @Test
    fun importStringList_lines_trim() {
        val text = "einz  \n   zwei \n drei \n  vier"
        val list = importStringList(text)
        expect that list containsExactly listOf("einz", "zwei", "drei", "vier")
    }

    @Test
    fun importStringList_lines_doubleLineBreaks() {
        val text = "einz  \n \n   zwei \n drei \n \n vier"
        val list = importStringList(text)
        expect that list containsExactly listOf("einz", "zwei", "drei", "vier")
    }

    @Test
    fun importStringList_combineCommasAndLines() {
        val text = "einz, zwei, drei\n vier, fünf\n "
        val list = importStringList(text)
        expect that list containsExactly listOf("einz", "zwei", "drei", "vier", "fünf")
    }

    @Test
    fun importStringList_empty() {
        val list = importStringList(" \t \n  ")
        expect that list iz empty
    }

}
