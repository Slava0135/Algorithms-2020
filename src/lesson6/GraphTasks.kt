@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson6

import lesson6.impl.GraphBuilder
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */

// Time: O(V + E)
fun Graph.findEulerLoop(): List<Graph.Edge> {

    if (edges.size == 0) return emptyList()

    val connections = mutableMapOf<Graph.Vertex, MutableSet<Graph.Vertex>>()
    for (vertex in vertices) {
        connections[vertex] = getNeighbors(vertex).toMutableSet()
        if (connections[vertex]!!.size % 2 != 0) return emptyList()
    }

    val currPath = ArrayDeque<Graph.Vertex>()
    currPath.add(vertices.first())
    val circuit = mutableListOf<Graph.Vertex>()

    while (currPath.isNotEmpty()) {
        val current = currPath.last()
        if (connections[current]!!.isNotEmpty()) {
            val next = connections[current]!!.first()
            connections[current]!!.remove(next)
            connections[next]!!.remove(current)
            currPath.addLast(next)
        } else {
            currPath.removeLast()
            circuit.add(current)
        }
    }

    if (connections.any { it.value.isNotEmpty() }) return emptyList()

    val result = mutableListOf<Graph.Edge>()
    for (i in 0 until circuit.size - 1) {
        result.add(getConnection(circuit[i], circuit[i + 1])!!)
    }
    return result
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан связный граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */

// Time: O(V + E)
fun Graph.minimumSpanningTree(): Graph {

    if (vertices.isEmpty()) return this

    val notProceeded = mutableListOf<Graph.Vertex>(vertices.first())
    val explored = mutableSetOf<String>()

    val graphBuilder = GraphBuilder().apply {

        val newVertices = mutableMapOf<String, Graph.Vertex>()

        val first = addVertex(vertices.first().name)
        newVertices[first.name] = first
        explored.add(first.name)

        while (notProceeded.isNotEmpty()) {
            val current = notProceeded.first()
            notProceeded.removeAt(0)
            for (node in getNeighbors(current)) {
                if (node.name !in explored) {

                    notProceeded.add(node)
                    explored.add(node.name)

                    val new = addVertex(node.name)
                    newVertices[new.name] = new
                    addConnection(newVertices[current.name]!!, new)

                }
            }
            explored.add(current.name)
        }
    }
    return graphBuilder.build()
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */

// Time: O(V + E), Space: O(V + E)
fun Graph.detectCycle(): Boolean {

    val connections = mutableMapOf<Graph.Vertex, MutableSet<Graph.Vertex>>()
    for (vertex in vertices) {
        connections[vertex] = getNeighbors(vertex).toMutableSet()
    }

    val vertices = vertices.toMutableSet()

    while (vertices.isNotEmpty()) {
        val iterator = vertices.iterator()
        var isFound = false
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (connections[next]!!.size <= 1) {
                iterator.remove()
                connections.remove(next)
                getNeighbors(next).forEach { connections[it]?.remove(next) }
                isFound = true
            }
        }
        if (!isFound) return true
    }
    return false
}

// Time: O(V + E), Space: O(V + E)
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {

    require(!detectCycle())

    val connections = mutableMapOf<Graph.Vertex, MutableSet<Graph.Vertex>>()
    for (vertex in vertices) {
        connections[vertex] = getNeighbors(vertex).toMutableSet()
    }

    val vertices = vertices.reversed().toMutableList()

    var neighbours = connections.maxBy { it.value.size }?.value?.size ?: 0

    while (neighbours > 0) {
        val removed = mutableListOf<Graph.Vertex>()
        for (vertex in vertices) {
            if (connections[vertex]!!.size == neighbours) {
                removed.add(vertex)
                connections.remove(vertex)
                getNeighbors(vertex).forEach { connections[it]?.remove(vertex) }
                break
            }
        }
        if (removed.isEmpty()) {
            neighbours--
        } else {
            vertices.removeAll(removed)
        }
    }

    return vertices.toSet()
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */

// Time (worst): O(N^N), Space: O(N)
fun Graph.longestSimplePath(): Path {

    val path = LinkedHashSet<Graph.Vertex>()
    var best: List<Graph.Vertex>? = null

    fun next() {
        var allFailed = true
        for (neighbour in getNeighbors(path.last())) {
            if (neighbour !in path) {
                path.add(neighbour)
                next()
                path.remove(neighbour)
                allFailed = false
            }
        }
        if (allFailed && (best == null || path.size > best!!.size)) best = path.toList()
    }

    for (vertex in vertices) {
        path.clear()
        path.add(vertex)
        next()
    }

    if (best == null) return Path()

    var result = Path(best!!.first())
    best!!.drop(1).forEach {
        result = Path(result, this, it)
    }

    return result
}

/**
 * Балда
 * Сложная
 *
 * Задача хоть и не использует граф напрямую, но решение базируется на тех же алгоритмах -
 * поэтому задача присутствует в этом разделе
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */

//Time: O(S * L * W), Space: O(S + W * L), где S - кол-во букв, L - сред. длина слова, W - размер словаря.
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    val grid = File(inputName).readLines().map { line -> line.split(" ").map { it.first() } }
    val result = mutableSetOf<String>()
    val offset = listOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0))
    for (word in words) {
        search@ for (i in grid.indices) {
            for (j in grid[0].indices) {
                if (grid[i][j] == word.first()) {
                    val visited = LinkedHashSet<Pair<Int, Int>>().apply { add(Pair(i, j)) }
                    fun find(x: Int, y: Int): Boolean {
                        if (visited.size == word.length) {
                            result.add(word)
                            return true
                        }
                        fun Pair<Int, Int>.isValid() = first in grid.indices && second in grid[0].indices
                        offset.forEach {
                            val next = Pair(x + it.first, y + it.second)
                            if (next.isValid() && grid[next.first][next.second] == word[visited.size] && next !in visited) {
                                visited.add(next)
                                if (find(next.first, next.second)) return true
                                visited.remove(next)
                            }
                        }
                        return false
                    }
                    if (find(i, j)) break@search
                }
            }
        }
    }
    return result
}