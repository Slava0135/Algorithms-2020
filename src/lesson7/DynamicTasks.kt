@file:Suppress("UNUSED_PARAMETER")

package lesson7

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */

// Time: avg./worst O(N * M), Memory: O(N * M)
fun longestCommonSubSequence(first: String, second: String): String {

    val grid = Array(first.length) { IntArray(second.length) { 0 } }

    var max = Pair(0, 0)
    var maxValue = 0

    for (i in grid.indices) {
        for (j in grid[0].indices) {
            if (first[i] == second[j]) {
                if (j > 0 && i > 0) {
                    grid[i][j] = grid[i - 1][j - 1]
                }
                grid[i][j]++
                if (grid[i][j] > maxValue) {
                    max = Pair(i, j)
                    maxValue = grid[i][j]
                }
            } else {
                var copy = 0
                if (i > 0 && grid[i - 1][j] > copy) copy = grid[i - 1][j]
                if (j > 0 && grid[i][j - 1] > copy) copy = grid[i][j - 1]
                grid[i][j] = copy
            }
        }
    }

    val result = StringBuilder()
    var (x, y) = max
    while (grid[x][y] > 0) {
        if (x > 0 && grid[x - 1][y] == grid[x][y]) {
            x--
        } else if (y > 0 && grid[x][y - 1] == grid[x][y]) {
            y--
        } else if (x > 0 && y > 0 && grid[x - 1][y - 1] == grid[x][y] - 1) {
            result.append(first[x])
            x--
            y--
        } else {
            result.append(first[x])
            break
        }
    }
    return result.reversed().toString()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {

    if (list.isEmpty()) return emptyList()

    val chain = mutableListOf<Pair<Int, Int?>>()

    for (i in list.indices) {
        chain.add(Pair(1, null))
        for (j in 0 until i) {
            if (list[i] > list[j] && chain[j].first + 1 > chain[i].first) {
                chain[i] = Pair(chain[j].first + 1, j)
            }
        }
    }

    var num = chain.maxBy { it.first }!!
    val result = mutableListOf(list[chain.indexOf(num)])
    while (num.second != null) {
        result.add(list[num.second!!])
        num = chain[num.second!!]
    }

    return result.reversed()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5