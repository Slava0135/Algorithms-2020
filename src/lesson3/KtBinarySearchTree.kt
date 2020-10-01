package lesson3

import java.util.*
import kotlin.ConcurrentModificationException
import kotlin.IllegalStateException
import kotlin.NoSuchElementException
import kotlin.collections.ArrayDeque
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    private var operations = 0
    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        operations++
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */

    // Time: avg. O(log N), worst O(N)
    override fun remove(element: T): Boolean {

        fun findParent(start: Node<T>): Node<T>? {
            if (element.compareTo(start.value) < 0) {
                if (start.left == null) return null
                if (start.left!!.value == element) return start
                return findParent(start.left!!)
            }
            if (element.compareTo(start.value) > 0) {
                if (start.right == null) return null
                if (start.right!!.value == element) return start
                return findParent(start.right!!)
            }
            throw IllegalStateException()
        }

        fun replace(target: Node<T>): Node<T> {
            var parent = target
            var successor = parent.right!!
            while (successor.left != null) {
                parent = successor
                successor = parent.left!!
            }

            if (parent != target) {
                parent.left = successor.right
            }

            successor.left = target.left
            if (target.right != successor) {
                successor.right = target.right
            }
            return successor
        }

        operations++
        if (root == null) return false

        if (root!!.value == element) {
            root = if (root!!.left != null && root!!.right != null) {
                replace(root!!)
            } else if (root!!.left != null) {
                root!!.left
            } else if (root!!.right != null) {
                root!!.right
            } else {
                null
            }
            size--
            return true
        }

        val parent = findParent(root!!) ?: return false

        if (parent.left?.value == element) {
            val node = parent.left!!
            if (node.left != null && node.right != null) {
                parent.left = replace(node)
            } else if (node.left != null) {
                parent.left = node.left
            } else if (node.right != null) {
                parent.left = node.right
            } else parent.left = null
        } else {
            val node = parent.right!!
            if (node.left != null && node.right != null) {
                parent.right = replace(node)
            } else if (node.left != null) {
                parent.right = node.left
            } else if (node.right != null) {
                parent.right = node.right
            } else parent.right = null
        }
        size--
        return true
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {

        private val queue = ArrayDeque<Node<T>>()
        private var last: T? = null
        private var expectedOperations = operations

        init {
            if (root != null) {
                queue.addFirst(root!!)
                var left = root!!
                while (left.left != null) {
                    left = left.left!!
                    queue.addFirst(left)
                }
            }
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */

        override fun hasNext() = queue.isNotEmpty()

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */
        override fun next(): T {
            if (queue.isEmpty()) throw NoSuchElementException()

            val lastNode = queue.first()
            queue.removeFirst()

            last = lastNode.value

            if (lastNode.right != null) {
                queue.addFirst(lastNode.right!!)
                lastNode.right!!.left?.let {
                    var left = lastNode.right!!.left!!
                    queue.addFirst(left)
                    while (left.left != null) {
                        left = left.left!!
                        queue.addFirst(left)
                    }
                }
            }

            checkForCmodification()
            return last!!
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */
        override fun remove() {
            if (last == null) throw NoSuchElementException()
            checkForCmodification()
            expectedOperations++
            remove(last)
            last = null
        }

        private fun checkForCmodification() {
            if (expectedOperations != operations) throw ConcurrentModificationException()
        }
    }

    private class SubSet<T : Comparable<T>>(
        val tree: KtBinarySearchTree<T>,
        val from: T?,
        val to: T?
    ) : AbstractMutableSet<T>(), SortedSet<T> {

        override var size = -1
            private set
            get() = iterator().asSequence().count()

        // Time: avg. O(log N), worst O(N)
        override fun add(element: T): Boolean {
            require(isValid(element))
            return tree.add(element)
        }
        // Time: avg. O(log N), worst O(N)
        override fun remove(element: T): Boolean {
            require(isValid(element))
            return tree.remove(element)
        }

        override fun contains(element: T) = isValid(element) && tree.contains(element)

        fun isValid(element: T): Boolean = isAbove(element) && isBelow(element)

        fun isBelow(element: T) = to == null || to.compareTo(element) > 0
        fun isAbove(element: T) = from == null || from.compareTo(element) <= 0

        fun goRightUntilAbove(start: Node<T>): Node<T>? {
            var next = start
            while (next.right != null) {
                if (isAbove(next.right!!.value)) {
                    return next.right!!
                } else {
                    next = next.right!!
                }
            }
            return null
        }

        fun goLeftUntilBelow(start: Node<T>): Node<T>? {
            var next = start
            while (next.left != null) {
                if (isBelow(next.left!!.value)) {
                    return next.left!!
                } else {
                    next = next.left!!
                }
            }
            return null
        }

        inner class SubSetIterator internal constructor() : MutableIterator<T> {

            private val queue = ArrayDeque<Node<T>>()
            private var last: T? = null
            private var expectedOperations = tree.operations

            private fun addAllLeft(start: Node<T>) {
                var node: Node<T>? = start
                while (node!!.left != null) {
                    if (!isAbove(node.left!!.value)) {
                        node = goRightUntilAbove(node.left!!)
                        if (node == null) {
                            break
                        } else {
                            queue.addFirst(node)
                        }
                    } else {
                        queue.addFirst(node.left!!)
                        node = node.left!!
                    }
                }
            }

            init {
                if (tree.root != null) {
                    val root = tree.root!!
                    var node = root
                    while (true) {
                        if (isValid(node.value)) {
                            queue.addFirst(node)
                            addAllLeft(node)
                            break
                        } else if (!isBelow(node.value)) {
                            node = goLeftUntilBelow(node) ?: break
                        } else if (!isAbove(node.value)) {
                            node = goRightUntilAbove(node) ?: break
                        }
                    }
                }
            }

            override fun hasNext() = queue.isNotEmpty()

            override fun next(): T {
                if (queue.isEmpty()) throw NoSuchElementException()

                val lastNode = queue.first()
                queue.removeFirst()

                last = lastNode.value

                if (lastNode.right != null) {
                    if (isBelow(lastNode.right!!.value)) {
                        queue.addFirst(lastNode.right!!)
                        addAllLeft(lastNode.right!!)
                    } else {
                        val node = goLeftUntilBelow(lastNode.right!!)
                        if (node != null) {
                            queue.addFirst(node)
                            addAllLeft(node)
                        }
                    }
                }

                checkForCmodification()
                return last!!
            }

            override fun remove() {
                if (last == null) throw NoSuchElementException()
                checkForCmodification()
                expectedOperations++
                remove(last)
                last = null
            }

            private fun checkForCmodification() {
                if (expectedOperations != tree.operations) throw ConcurrentModificationException()
            }
        }

        override fun iterator(): MutableIterator<T> = SubSetIterator()

        override fun comparator(): Comparator<in T>? = null

        override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
            require(fromElement.compareTo(toElement) <= 0)
            require(from == null || from.compareTo(fromElement) <= 0)
            require(to == null || to.compareTo(toElement) >= 0)
            return SubSet(tree, fromElement, toElement)
        }

        override fun headSet(toElement: T): SortedSet<T> {
            require(to == null || to.compareTo(toElement) >= 0)
            require(isAbove(toElement))
            return SubSet(tree, null, toElement)
        }

        override fun tailSet(fromElement: T): SortedSet<T> {
            require(from == null || from.compareTo(fromElement) <= 0)
            require(isBelow(fromElement))
            return SubSet(tree, fromElement, null)
        }

        override fun first(): T {
            if (tree.root == null) throw NoSuchElementException()
            var node = tree.root!!
            if (!isAbove(node.value)) {
                if (node.right == null) {
                    throw NoSuchElementException()
                } else {
                    val right = goRightUntilAbove(node) ?: throw NoSuchElementException()
                    node = right
                }
            }

            while (node.left != null) {
                if (!isAbove(node.left!!.value)) {
                    val right = goRightUntilAbove(node.left!!)
                    if (right != null) {
                        node = right
                    } else {
                        break
                    }
                } else {
                    node = node.left!!
                }
            }

            if (!isValid(node.value)) throw NoSuchElementException()
            return node.value
        }

        override fun last(): T {
            if (tree.root == null) throw NoSuchElementException()
            var node = tree.root!!
            if (!isBelow(node.value)) {
                if (node.left == null) {
                    throw NoSuchElementException()
                } else {
                    val left = goLeftUntilBelow(node) ?: throw NoSuchElementException()
                    node = left
                }
            }

            while (node.right != null) {
                if (!isBelow(node.right!!.value)) {
                    val left = goLeftUntilBelow(node.right!!)
                    if (left != null) {
                        node = left
                    } else {
                        break
                    }
                } else {
                    node = node.right!!
                }
            }

            if (!isValid(node.value)) throw NoSuchElementException()
            return node.value
        }

    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */

    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        require(fromElement.compareTo(toElement) <= 0)
        return SubSet(this, fromElement, toElement)
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> = SubSet(this, null, toElement)

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> = SubSet(this, fromElement, null)

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }
}