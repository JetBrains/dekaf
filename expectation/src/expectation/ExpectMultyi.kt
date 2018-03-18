package org.jetbrains.dekaf.expectation

import java.util.*


/// COLLECTION \\\

val<E:Any,T:Any> MultiMatter<E,T>.beEmpty: MultiMatter<E,T>
    get() = expecting("an empty collection")
            .beNotNull
            .satisfy { elements.isEmpty() }

val<E:Any,T:Any> MultiMatter<E,T>.beEmptyOrNull: MultiMatter<E,T>
    get() = if (elements.isEmpty()) this
            else blame("an empty collection or null")

val<E:Any,T:Any> MultiMatter<E,T>.notBeEmpty: MultiMatter<E,T>
    get() = expecting("a non-empty collection")
            .satisfy { elements.isNotEmpty() }

fun<E:Any,T:Any> MultiMatter<E,T>.hasSize(n: Int): MultiMatter<E,T> =
        expecting("a collection with $n elements")
                .satisfy { elements.size == n }


fun<E:Any,T:Any> MultiMatter<E,T>.contain(vararg desiredElements: E): MultiMatter<E,T> =
        this.contain(listOf(*desiredElements))

fun<E:Any,T:Any> MultiMatter<E,T>.contain(desiredElements: Collection<E>): MultiMatter<E,T> =
        also {
            val simpleExpectText = "contains following ${desiredElements.size} elements:\n\t" +
                    desiredElements.joinToString(separator = "\n\t", transform = Any::displayString)
            expecting(simpleExpectText).beNotNull
        }.
        also {
            if (desiredElements.isEmpty()) return@also

            val pending = LinkedHashSet(desiredElements)
            val iterator = elements.iterator()
            while(pending.isNotEmpty() && iterator.hasNext()) {
                val element = iterator.next()
                pending.remove(element)
            }

            if (pending.isNotEmpty()) {
                val expect = "contains ${desiredElements.size} elements, but ${pending.size} of them not found:" +
                        desiredElements.joinToString(separator = "") { 
                            "\n\t" + it.displayString() + (if (it in pending) " <- not found" else "")
                        }
                blame(expect = expect)
            }
        }


fun<E:Any,T:Any> MultiMatter<E,T>.containExactly(vararg desiredElements: E): MultiMatter<E,T> =
        this.containExactly(Arrays.asList(*desiredElements))

fun<E:Any,T:Any> MultiMatter<E,T>.containExactly(desiredElements: Iterable<E>): MultiMatter<E,T> {
    val actualElements = elements
    val expectElements = desiredElements.toList()
    val n = expectElements.size
    var failed = actualElements.size != n
    var i = 0
    while (i < n && !failed) {
        val actElement = actualElements[i]
        val expElement = expectElements[i]
        failed = !Objects.equals(actElement, expElement)
        i++
    }

    if (failed) {
        val actualText = "${actualElements.size} elements:\n\t" +
                actualElements.joinToString(separator = "\n\t", transform = Any::displayString)
        val expectText = "${expectElements.size} elements:\n\t" +
                expectElements.joinToString(separator = "\n\t", transform = Any::displayString)
        blame(expect = expectText, actual = actualText, diff = true)
    }

    return this
}




fun<E:Any,T:Any> MultiMatter<E,T>.notContain(vararg undesiredElements: E): MultiMatter<E,T> =
        notContain(setOf(*undesiredElements))

fun<E:Any,T:Any> MultiMatter<E,T>.notContain(undesiredElements: Collection<E>): MultiMatter<E,T> =
        notContain(undesiredElements.toSet())

fun<E:Any,T:Any> MultiMatter<E,T>.notContain(undesiredElements: Set<E>): MultiMatter<E,T> =
        expecting(undesiredElements.elementsText("a collection not containing the following ${undesiredElements.size} elements:"))
                .checkElements(containerWord = "collection",
                               predicateDescription = "doesn't contain undesired element",
                               predicate = { it !in undesiredElements })

fun<E:Any,T:Any> MultiMatter<E,T>.everyElementSatisfy(predicate: (E) -> Boolean): MultiMatter<E,T> =
        everyElementSatisfy("specified predicate", predicate)

fun<E:Any,T:Any> MultiMatter<E,T>.everyElementSatisfy(predicateDescription: String, predicate: (E) -> Boolean): MultiMatter<E,T> =
        expecting("every element: $predicateDescription")
                .checkElements(containerWord = "collection",
                               predicateDescription = predicateDescription,
                               predicate = predicate)


