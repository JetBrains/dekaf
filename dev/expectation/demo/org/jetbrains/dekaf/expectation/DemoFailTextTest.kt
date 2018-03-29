package org.jetbrains.dekaf.expectation

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test

@Tags(Tag("demo"), Tag("fail"))
class DemoFailTextTest {

    @Test
    fun `diff`() {
        val text1 = """
                    |Gravitation cannot be held responsible for people falling in love.
                    |How on
                    |earth can you explain in terms of chemistry and physics so important a
                    |biological phenomenon as first love? Put your hand on a stove for a minute
                    |and it seems like an hour. Sit with that special girl for an hour and it seems
                    |like a minute. That's relativity.
                    |       Albert Einstein
                    """.trimMargin()
        val text2 = """
                    |Gravitation cannot be held responsible for people falling in love.
                    |How on earth can you explain in terms of chemistry and physics so important
                    |a biological phenomenon as first love? Put your hand on a stove for a minute
                    |and it seems like an hour. Sit with that special girl for an hour
                    |and it seems like a minute. That's relativity.
                    |       Albert Einstein
                    """.trimMargin()
        text1.must.be(text2)
    }

    @Test
    fun `match`() {
        val text = "Do not worry about your difficulties in Mathematics. I can assure you mine are still greater."
        text.must.match(".*Albert Einstein.*")
    }

    @Test
    fun `contain`() {
        val text = "Do not worry about your difficulties in Mathematics. I can assure you mine are still greater."
        text.must.contain("Albert Einstein")
    }

    @Test
    fun `contain query sign`() {
        val text = "Do not worry about your difficulties in Mathematics. I can assure you mine are still greater."
        text.must.contain('?')
    }

    @Test
    fun `contain ignoring case`() {
        val text = "Do not worry about your difficulties in Mathematics. I can assure you mine are still greater."
        text.must.contain("Albert Einstein", ignoreCase = true)
    }

    @Test
    fun `not contain char`() {
        val text = "Imagination is more important than knowledge…"
        text.must.notContain('…')
    }

    @Test
    fun `extract`() {
        val text = """|Every day I remind myself that my inner and outer life
                      |are based on the labors of other men, living and dead,
                      |and that I must exert myself in order to give in the same measure
                      |as I have received and am still receiving.
                   """.trimMargin()
        text.must
                .extractingOne(Regex("my(.*)life"), 1) { contain("inner").contain("outer") }
                .extractingOne(Regex("I have(.*)\\."), 0) { contain("day") }
    }



    
}