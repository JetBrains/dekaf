package org.jetbrains.dekaf.util


class RepeatingList<out T>
(
        /**
         * The value that is repeated.
         */
        private val value: T,

        /**
         * The size of the list.
         */
        override val size: Int
)
    : List<T>
{

    init {
        if (size < 0) throw IllegalArgumentException("Attempted to make a list with negative size: $size")
    }

    override fun get(index: Int): T =
            if (index >= 0 && index < size) value
            else throw IndexOutOfBoundsException("Attempted to get element with index $index from a list of $size elements")

    override fun indexOf(element: @UnsafeVariance T) =
            if (element == value) 0
            else -1

    override fun lastIndexOf(element: @UnsafeVariance T) =
            if (element == value) 0
            else -1

    override fun isEmpty() = size == 0

    override fun contains(element: @UnsafeVariance T) = element == value

    override fun containsAll(elements: Collection<@UnsafeVariance T>): Boolean {
        for (element in elements) if (element != value) return false
        return true
    }

    override fun subList(fromIndex: Int, toIndex: Int) = RepeatingList(value, toIndex - fromIndex)

    override fun iterator() = RepeatingIterator(value, size)

    override fun listIterator() = RepeatingIterator(value, size)

    override fun listIterator(index: Int) = RepeatingIterator(value, size - index)

}


class RepeatingIterator<out T>
(
        /**
         * The value that is repeated.
         */
        private val value: T,

        /**
         * The size of the list.
         */
        private val size: Int
)
    : ListIterator<T>
{
    var position: Int = 0

    override fun hasNext() = position < size

    override fun hasPrevious() = position > 0

    override fun next(): T {
        position++
        return value
    }

    override fun nextIndex() = position

    override fun previous(): T {
        position--
        return value
    }

    override fun previousIndex() = position-1
}

