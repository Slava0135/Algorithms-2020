package lesson4

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var operations = 0
    private var root = Node()

    override var size: Int = 0
        private set(value) {
            operations++
            field = value
        }

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        fun findNodeAndLastFork(element: String): Triple<Node, Node?, Char?>? {
            var current = root
            var lastFork: Node? = null
            var lastForked: Char? = null
            for (char in element) {
                if (current.children.size > 1) {
                    lastFork = current
                    lastForked = char
                }
                current = current.children[char] ?: return null
            }
            if (0.toChar() !in current.children.keys) return null

            return if (current.children.size > 1) {
                Triple(current, null, null)
            } else Triple(current, lastFork, lastForked)
        }

        val (current, lastFork, lastForked) = findNodeAndLastFork(element) ?: return false
        current.children.remove(0.toChar())
        lastFork?.children?.remove(lastForked)
        size--
        return true
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> = TrieIterator()

    private inner class TrieIterator : MutableIterator<String> {

        var line = StringBuilder()
        val queue = ArrayDeque<Pair<Node, String>>()

        var last: String? = null
        var expectedOperations = operations

        init {
            var left = root
            while (left.children.keys.isNotEmpty()) {
                for ((char, node) in left.children.entries.drop(1)) {
                    queue.addFirst(Pair(node, line.toString() + char))
                }
                line.append(left.children.keys.first())
                left = left.children.values.first()
            }
            if (left != root) queue.addFirst(Pair(left, line.toString()))
        }

        override fun hasNext() = queue.isNotEmpty()

        override fun next(): String {
            checkForCmodification()
            if (queue.isEmpty()) throw NoSuchElementException()

            var (left, string) = queue.first()
            queue.removeFirst()
            if (string.last() == 0.toChar()) {
                last = string.dropLast(1)
                return last!!
            }
            line = StringBuilder(string)
            while (left.children.keys.isNotEmpty()) {
                for ((char, node) in left.children.entries.drop(1)) {
                    queue.addFirst(Pair(node, line.toString() + char))
                }
                line.append(left.children.keys.first())
                left = left.children.values.first()
            }
            last = line.toString().dropLast(1)
            return last!!
        }

        override fun remove() {
            checkForCmodification()
            if (last == null) throw IllegalStateException()
            expectedOperations++
            remove(last)
            last = null
        }

        private fun checkForCmodification() {
            if (expectedOperations != operations) throw ConcurrentModificationException()
        }
    }
}

fun main() {
    val a = KtTrie()
    a.addAll(listOf("fc", "fcfbehhagfgdg"))
    println(a)
    a.remove("fc")
    println(a)
}