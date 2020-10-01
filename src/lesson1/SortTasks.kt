@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.lang.Math.abs

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

private fun <T> save(outputName: String, list: List<T>) {
    require(list.isNotEmpty())
    File(outputName).bufferedWriter().use {
        val iter = list.iterator()
        while (iter.hasNext()) {
            it.write(iter.next().toString())
            if (iter.hasNext()) {
                it.newLine()
            }
        }
    }
}

// Time: avg./worst O(N * log N), Memory: O(N)
fun sortTimes(inputName: String, outputName: String) {
    class Time(line: String) : Comparable<Time> {
        val hours: Int
        val minutes: Int
        val seconds: Int
        val m: String

        init {
            require(Regex("""\d\d:\d\d:\d\d ((AM)|(PM))""").matches(line))
            val list = line.split(' ', ':')
            val h = list.first().toInt()
            require(h in 0..12)
            m = list.last()
            hours = h + if (m == "PM" && h != 12) 12 else 0 - if (m == "AM" && h == 12) 12 else 0
            minutes = list[1].toInt()
            require(minutes in 0..59)
            seconds = list[2].toInt()
            require(seconds in 0..59)
        }

        override fun compareTo(other: Time): Int {
            if (hours > other.hours) return 1
            if (hours < other.hours) return -1

            if (minutes > other.minutes) return 1
            if (minutes < other.minutes) return -1

            if (seconds > other.seconds) return 1
            if (seconds < other.seconds) return -1

            return 0
        }

        override fun toString() =
            ((hours + (if (m == "AM" && hours == 0) 12 else 0) - (if (m == "PM" && hours != 12) 12 else 0)).toString()
                .padStart(2, '0')
                    + ":" + minutes.toString().padStart(2, '0')
                    + ":" + seconds.toString().padStart(2, '0')
                    + " " + m)
    }
    save(outputName, File(inputName).readLines().map { Time(it) }.sorted())
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

// Time: avg./worst O(N * log N), Memory: O(N)
fun sortAddresses(inputName: String, outputName: String) {

    class Resident(val firstName: String, val secondName: String) : Comparable<Resident> {
        override fun toString() = firstName + " " + secondName
        override fun compareTo(other: Resident): Int {
            if (firstName > other.firstName) return 1
            if (firstName < other.firstName) return -1

            if (secondName > other.secondName) return 1
            if (secondName < other.secondName) return -1

            return 0
        }
    }

    class Location(line: String) : Comparable<Location> {
        val resident: Resident
        val name: String
        val number: Int

        private val regex =
            Regex("""([А-ЯЁA-Z][а-яёa-z]*)+ ([А-ЯЁA-Z][а-яёa-z]*)+ - [А-ЯЁA-Z][а-яёa-z]*(-?[А-ЯЁA-Z][а-яёa-z]*)* \d+""")

        init {
            if (!regex.matches(line)) println(line)
            require(regex.matches(line))
            val list = line.split(" - ", " ")
            resident = Resident(list[0], list[1])
            name = list[2]
            number = list[3].toInt()
        }

        override fun compareTo(other: Location): Int {
            if (name > other.name) return 1
            if (name < other.name) return -1

            if (number > other.number) return 1
            if (number < other.number) return -1

            return 0
        }
    }

    class LocationCombined(location: Location) {
        val name = location.name
        val number = location.number
        val list = mutableListOf(location.resident)

        override fun toString() = name + " " + number + " - " + list.sorted().joinToString(", ")
    }

    val locations = File(inputName).readLines().map { Location(it) }.sorted()
    val combined = mutableListOf<LocationCombined>()

    var prev = locations.first()
    combined.add(LocationCombined(prev))
    for (location in locations.drop(1)) {
        if (location.name == prev.name && location.number == prev.number) {
            combined.last().list.add(location.resident)
        } else {
            combined.add(LocationCombined(location))
            prev = location
        }
    }
    save(outputName, combined)
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */

// Time: avg./worst O(low + high), Memory: O(low + high)
fun sortTemperatures(inputName: String, outputName: String) {
    val low = -2730
    val high = 5000
    val lines = File(inputName).readLines().map { it.replace(".", "").toInt() - low }.toIntArray()
    val sorted = countingSort(lines, high - low)
    save(outputName, sorted.map {
        val actual = it + low
        val i = kotlin.math.abs(actual / 10).toString()
        val f = kotlin.math.abs(actual % 10).toString()
        if (actual < 0) {
            "-$i.$f"
        } else {
            "$i.$f"
        }
    })
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */

// Time: avg./worst O(N), Memory: O(N)
fun sortSequence(inputName: String, outputName: String) {
    val list = File(inputName).readLines().map { it.toInt() }
    val count = mutableMapOf<Int, Int>()
    for (num in list) {
        count[num] = (count[num] ?: 0) + 1
    }
    var freq = count.iterator().next()
    for (num in count) {
        if (num.value > freq.value || num.value == freq.value && num.key < freq.key) {
            freq = num
        }
    }
    save(outputName, list.filter { it != freq.key } + generateSequence { freq.key }.take(freq.value))
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */

// Time: avg./worst O(N), Memory: O(N)
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    var iOne = 0
    var iTwo = first.size
    var index = 0
    while (index < second.size) {
        if (iOne == first.size) {
            second[index++] = second[iTwo++]
            continue
        }
        if (iTwo == second.size) {
            second[index++] = first[iOne++]
            continue
        }
        if (first[iOne] < second[iTwo]!!) {
            second[index++] = first[iOne++]
        } else {
            second[index++] = second[iTwo++]
        }
    }
}

