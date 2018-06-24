package org.jetbrains.dekaf.expectation


val TextMatter.beEmptyOrNull: Unit
    get() {
        if (text.isNotEmpty()) blame("an empty text or null")
    }

val TextMatter.beEmpty: TextMatter
    get() = beNotNull.also {
        if (text.isNotEmpty()) blame("an empty text")
    }

val TextMatter.beNotEmpty: TextMatter
    get() = beNotNull.also {
        if (text.isEmpty()) blame("not empty text")
    }


fun TextMatter.be(another: String, ignoreCase: Boolean = false, compressSpace: Boolean = false): TextMatter {
    val txtAct = if (compressSpace) text.compressSpaces() else text
    val txtExp = if (compressSpace) another.compressSpaces() else another
    val ok = txtAct.equals(txtExp, ignoreCase = ignoreCase)
    if (ok) return this
    else blame(another.displayText(), diff = true)
}

fun TextMatter.notBe(another: String, ignoreCase: Boolean = false, compressSpace: Boolean = false): TextMatter {
    val txtAct = if (compressSpace) text.compressSpaces() else text
    val txtExp = if (compressSpace) another.compressSpaces() else another
    val ok = txtAct.equals(txtExp, ignoreCase = ignoreCase)
    if (!ok) return this
    else blame("another text (not this)")
}


fun TextMatter.match(regex: String): TextMatter =
        match(Regex(regex))

fun TextMatter.match(regex: Regex): TextMatter =
        if (regex.matches(text)) this
        else blame("text matches the following regular expression: \n\t${regex.pattern}")

fun TextMatter.notMatch(regex: String): TextMatter =
        match(Regex(regex))

fun TextMatter.notMatch(regex: Regex): TextMatter =
        if (regex.matches(text)) this
        else blame("text that doesn't match the following regular expression: \n\t${regex.pattern}")


fun TextMatter.contain(char: Char, ignoreCase: Boolean = false): TextMatter =
        if (text.contains(char, ignoreCase)) this
        else blame("text containing ${if (ignoreCase) "(ignoring case) " else ""}the character ${char.displayString()}")

fun TextMatter.contain(string: String, ignoreCase: Boolean = false): TextMatter =
        if (text.contains(string, ignoreCase)) this
        else blame("text containing ${if (ignoreCase) "(ignoring case) " else ""}the following: " + string.displayText())

fun TextMatter.contain(regex: Regex): TextMatter =
        if (regex.containsMatchIn(text)) this
        else blame("text containing a subtext matching the following regular expression: \n\t" + regex.pattern)

fun TextMatter.notContain(char: Char, ignoreCase: Boolean = false): TextMatter =
        if (!text.contains(char, ignoreCase)) this
        else blame("text that doesn't contain ${if (ignoreCase) "(ignoring case) " else ""}the character ${char.displayString()}")

fun TextMatter.notContain(string: String, ignoreCase: Boolean = false): TextMatter =
        if (!text.contains(string, ignoreCase)) this
        else blame("text that doesn't contain ${if (ignoreCase) "(ignoring case) " else ""} the following: " + string.displayText())

fun TextMatter.notContain(regex: Regex): TextMatter =
        if (!regex.containsMatchIn(text)) this
        else blame("text that doesn't contain a subtext matching the following regular expression: \n\t" + regex.pattern)


fun TextMatter.startWith(prefix: String): TextMatter =
        if (text.startsWith(prefix)) this
        else blame("text starting with the following: " + prefix.displayText())

fun TextMatter.notStartWith(prefix: String): TextMatter =
        if (!text.startsWith(prefix)) this
        else blame("text doesn't start with the following: " + prefix.displayText())

fun TextMatter.endsWith(suffix: String): TextMatter =
        if (text.endsWith(suffix)) this
        else blame("text ending with the following: " + suffix.displayText())

fun TextMatter.notEndsWith(suffix: String): TextMatter =
        if (!text.endsWith(suffix)) this
        else blame("text doesn't end with the following: " + suffix.displayText())


fun TextMatter.haveLength(length: Int): TextMatter =
        if (text.length == length) this
        else blame("text length $length")


/// TRANSFORMERS \\\

fun TextMatter.extractingOne(regex: Regex, groupNumber: Int = 0, body: TextMatter.() -> Unit): TextMatter {
    val m = regex.find(text)
            ?: blame("text includes a subtext matching the following pattern:\n\t${regex.pattern}")
    val group = m.groups[groupNumber]
            ?: blame(actual = null, expect = null,
                     details = "the following pattern has no group number $groupNumber:\n\t${regex.pattern}")
    val newText = group.value
    val newMatter = transform(newText)
    newMatter.body()
    return this
}

fun TextMatter.extractingOne(regex: Regex, groupName: String, body: TextMatter.() -> Unit): TextMatter {
    val m = regex.find(text)
            ?: blame("text includes a subtext matching the following pattern:\n\t${regex.pattern}")
    val group = m.groups[groupName]
            ?: blame(actual = null, expect = null,
                     details = "the following pattern has no group name $groupName:\n\t${regex.pattern}")
    val newText = group.value
    val newMatter = transform(newText)
    newMatter.body()
    return this
}



/// INTERNAL FUNCTIONS \\\

internal fun String.compressSpaces(): String {
    if (this.isEmpty()) return this
    val newString = this.trim().replace(SpaceCompressingRegex, " ")
    return if (newString.length == this.length) this else newString
}

internal val SpaceCompressingRegex =
        Regex("""\s{2,}""", RegexOption.DOT_MATCHES_ALL)
