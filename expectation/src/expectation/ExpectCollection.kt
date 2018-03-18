package org.jetbrains.dekaf.expectation

import java.util.*


/// COLLECTION \\\

val<E:Any, M:Iterable<E>> Matter<M>.beEmpty: Matter<M>
    get() = expecting("an empty collection")
            .satisfy { !thing.iterator().hasNext() }

val<E:Any, M:Iterable<E>> Matter<M>.beEmptyOrNull: Matter<M>
    get() = if (something == null || !something.iterator().hasNext()) this
            else blame("an empty collection or null")

val<E:Any, M:Iterable<E>> Matter<M>.notBeEmpty: Matter<M>
    get() = expecting("a non-empty collection")
            .satisfy { thing.isNotEmpty() }

fun<E:Any, M:Iterable<E>> Matter<M>.hasSize(n: Int): Matter<M> =
        expecting("a collection with $n elements")
                .satisfy { thing.countSize() == n }


fun<E:Any, M:Iterable<E>> Matter<M>.contain(vararg elements: E): Matter<M> =
        this.contain(listOf(*elements))

fun<E:Any, M:Iterable<E>> Matter<M>.contain(desiredElements: Collection<E>): Matter<M> =
        with {
            if (desiredElements.isEmpty()) return@with

            val simpleExpectText = "contains following ${desiredElements.size} elements:\n\t" +
                    desiredElements.joinToString(separator = "\n\t", transform = Any::displayString)
            val container = thing(simpleExpectText)
            val iterator = container.iterator()
            if (!iterator.hasNext()) blame(simpleExpectText)

            val pending = LinkedHashSet(desiredElements)
            while(pending.isNotEmpty() && iterator.hasNext()) {
                val element = iterator.next()
                pending.remove(element)
            }

            if (pending.isNotEmpty()) {
                val expect = "contains ${desiredElements.size} elements, but ${pending.size} of them not found:" +
                        desiredElements.joinToString(separator = "") { it ->
                            "\n\t" + it.displayString() + (if (it in pending) " <- not found" else "")
                        }
                blame(expect = expect)
            }
        }


fun<E:Any, M:Iterable<E>> Matter<M>.containExactly(vararg desiredElements: E): Matter<M> =
        this.containExactly(Arrays.asList(*desiredElements))

fun<E:Any, M:Iterable<E>> Matter<M>.containExactly(desiredElements: Iterable<E>): Matter<M> {
    val actualElements = something.toList()
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




fun<E:Any, M:Iterable<E>> Matter<M>.notContain(vararg undesiredElements: E): Matter<M> =
        notContain(setOf(*undesiredElements))

fun<E:Any, M:Iterable<E>> Matter<M>.notContain(undesiredElements: Collection<E>): Matter<M> =
        notContain(undesiredElements.toSet())

fun<E:Any, M:Iterable<E>> Matter<M>.notContain(undesiredElements: Set<E>): Matter<M> =
        expecting(undesiredElements.elementsText("a collection not containing the following ${undesiredElements.size} elements:"))
                .checkElements(containerWord = "collection",
                               iterator = thing.iterator(),
                               predictedSize = something.predictSize(),
                               predicateDescription = "doesn't contain undesired element",
                               predicate = { it !in undesiredElements })

fun<E:Any, M:Iterable<E>> Matter<M>.everyElementSatisfy(predicate: (E) -> Boolean): Matter<M> =
        everyElementSatisfy("specified predicate", predicate)

fun<E:Any, M:Iterable<E>> Matter<M>.everyElementSatisfy(predicateDescription: String, predicate: (E) -> Boolean): Matter<M> =
        expecting("every element: $predicateDescription")
                .checkElements(containerWord = "collection",
                               iterator = thing.iterator(),
                               predictedSize = something.predictSize(),
                               predicateDescription = predicateDescription,
                               predicate = predicate)


