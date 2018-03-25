package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("ExpectationDemoTest")
class DemoSuccessTextTest {

    @Test
    fun `equal`() {
        val text = "Lorem" + ' ' + "Ipsum"
        text.must.be("Lorem Ipsum")
    }

    @Test
    fun `equal ignoring case`() {
        val text = "Lorem" + ' ' + "Ipsum"
        text.must.be("lorem ipsum", ignoreCase = true)
    }

    @Test
    fun `equal compressing spaces`() {
        val text = " Lorem" + "  " + "Ipsum "
        text.must.be("Lorem Ipsum", compressSpace = true)
    }

    @Test
    fun `match (1)`() {
        val text = "Do not worry about your difficulties in Mathematics. I can assure you mine are still greater."
        text.must.match("""^[A-Z].*\.$""")
    }

    @Test
    fun `match (2)`() {
        val text = "Do not worry about your difficulties in Mathematics. I can assure you mine are still greater."
        text.must.match(Regex("""^[A-Z].*\.$"""))
    }

    @Test
    fun `contain`() {
        val text = "Imagination is more important than knowledge…"
        text.must.contain("important")
    }

    @Test
    fun `contain ignoring case`() {
        val text = "Imagination is more important than knowledge…"
        text.must.contain("imagination", ignoreCase = true)
    }

    @Test
    fun `not contain char`() {
        val text = "Imagination is more important than knowledge…"
        text.must.notContain('!')
    }

    @Test
    fun `extract`() {
        val text = """|Every day I remind myself that my inner and outer life
                      |are based on the labors of other men, living and dead,
                      |and that I must exert myself in order to give in the same measure
                      |as I have received and am still receiving.
                   """".trimMargin()
        text.must
                .extractingOne(Regex("my(.*)life"), 1) { contain("inner").contain("outer") }
                .extractingOne(Regex("based on(.*),"), 1) { contain("labors") }
    }





}