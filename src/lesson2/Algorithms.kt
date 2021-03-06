@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

// Time: avg./worst O(N), Memory: O(N)
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val numbers = File(inputName).readLines().map { it.toInt() }
    require(numbers.all { it > 0 })
    require(numbers.size > 1)

    var min = numbers.first()
    var minPos = 0

    var result: Pair<Int, Int>? = null
    var maxDiff = 0

    var i = 1
    while (i < numbers.size) {
        if (numbers[i - 1] < min) {
            min = numbers[i - 1]
            minPos = i - 1
        }
        if (numbers[i] - min > maxDiff) {
            result = Pair(minPos, i)
            maxDiff = numbers[i] - min
        }
        i++
    }
    return Pair(result!!.first + 1, result.second + 1)
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */

// Time: avg./worst O(N), Memory: O(1)
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    var result = 0
    for (i in 1..menNumber) {
        result = (result + choiceInterval) % i
    }
    return result + 1
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */

// Time: avg./worst O(N * M), Memory: O(N * M), N и M - длины строк
fun longestCommonSubstring(first: String, second: String): String {
    val grid = Array(first.length) { IntArray(second.length) { 0 } }
    for (j in grid[0].indices) {
        if (first[0] == second[j]) {
            grid[0][j] = 1
        }
    }

    var maxF = 0
    var maxValue = 0
    for (i in 1 until grid.size) {
        for (j in grid[0].indices) {
            if (first[i] == second[j]) {
                if (j > 0) {
                    grid[i][j] = grid[i - 1][j - 1]
                }
                grid[i][j]++
                if (grid[i][j] > maxValue) {
                    maxF = i
                    maxValue = grid[i][j]
                }
            }
        }
    }
    return first.substring(maxF - (maxValue - 1)..maxF)
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */

// Time: avg./worst O(N * log(log N)), Memory: O(N)
fun calcPrimesNumber(limit: Int): Int {
    if (limit <= 1) return 0
    val marks = BooleanArray(limit + 1) { true }
    for (i in 2..limit) {
        if (marks[i]) {
            for (j in i * 2..limit step i) {
                marks[j] = false
            }
        }
    }
    return marks.count { it } - 2
}
