package org.jetbrains.dekaf

import org.jetbrains.dekaf.util.ArrayUtil
import org.jetbrains.dekaf.util.toFixString
import org.junit.jupiter.api.*
import java.lang.IllegalStateException
import java.lang.reflect.Method


internal const val javaSteps = 5
internal const val kotlinSteps = 3
internal const val demoPackage = "demo"

/**
 * Tests of Demo examples.
 */
@Tag("demo")
class DemoTest {

    @TestFactory  @DisplayName("java")
    fun java(): List<DynamicTest> {
        return prepareDemoTests("java", javaSteps)
    }

    @TestFactory  @DisplayName("kotlin")
    fun kotlin(): List<DynamicTest> {
        return prepareDemoTests("kotlin", kotlinSteps)
    }

    private fun prepareDemoTests(language: String, stepsCount: Int): ArrayList<DynamicTest> {
        val tests = ArrayList<DynamicTest>(stepsCount)
        for (i in 1..stepsCount) {
            val nr = i.toFixString(2)
            val name = "$demoPackage.$language.Step$nr"
            val claß = Class.forName(name)
                    ?: throw IllegalStateException("Demo class $name not found.")
            val method = claß.getDeclaredMethod("main", ArrayUtil.getArrayClass(String::class.java))
                    ?: throw IllegalStateException("""Demo class $name has no appropriate method "main".""")
            val runner = MainMethodRunner(claß, method)
            val test = DynamicTest.dynamicTest("Step$nr", runner)
            tests += test
        }
        return tests
    }

    private inner class MainMethodRunner : org.junit.jupiter.api.function.Executable {

        private val claß: Class<*>
        private val method: Method

        constructor(claß: Class<*>, method: Method) {
            this.claß = claß
            this.method = method
        }

        override fun execute() {
            try {
                method.isAccessible = true
                method.invoke(null, emptyArray<String>())
            }
            catch (e: Throwable) {
                fail("Failed to execute class ${claß.simpleName} method ${method.name}: exception ${e.javaClass.simpleName}: ${e.message}")
            }
        }

    }

}