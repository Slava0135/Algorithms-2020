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
        fun findNodeAndLastFork(element: String): Triple<Node, Node?, Char>? {
            var current = root
            var lastFork: Node? = null
            var lastForked = element.first()
            for (char in element) {
                val size = current.children.size
                if (size > 1) {
                    lastFork = current
                }
                current = current.children[char] ?: return null
                if (size > 1) {
                    lastForked = char
                }
            }
            return Triple(current, lastFork, lastForked)
        }

        val (current, lastFork, lastForked) = findNodeAndLastFork(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            lastFork?.children?.remove(lastForked)
            size--
            return true
        }
        return false
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

        val path = ArrayDeque<Node>()
        var last: Node? = null
        var expectedOperations = operations

        init {
            path.addFirst(root)
            var left = root
            while (left.children.keys.isNotEmpty()) {
                left = left.children.values.first()
                path.add(left)
            }
        }

        override fun hasNext() = path.isNotEmpty()

        override fun next(): String {
            if (path.isEmpty()) throw NoSuchElementException()
            checkForCmodification()
            return ""
        }

        override fun remove() {
            checkForCmodification()
            if (last == null) throw IllegalStateException()

            last = null
        }

        private fun checkForCmodification() {
            if (expectedOperations != operations) throw ConcurrentModificationException()
        }
    }

}