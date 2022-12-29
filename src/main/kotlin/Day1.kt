package d1

import measure
import printResult
import java.io.File

fun main(args: Array<String>) {
    val lines = File("input.txt").readLines()
    printResult(functional(lines))
    measure { functional(lines) }
    printResult(sane(lines))
    measure { sane(lines) }
}

fun functional(input: List<String>): Pair<Any, Any> {
    fun splitter(res: List<List<Int>>, input: List<String>): List<List<Int>> {
        val new = input.takeWhile { it != "" }.map { it.toInt() }
        return if (input.size == new.size) {
            res.plusElement(new)
        } else {
            splitter(res.plusElement(new), input.drop(new.size + 1))
        }
    }

    val cals = splitter(emptyList(), input).map { it.sum() }.sorted()
    return cals.last() to cals.takeLast(3).sum()
}

fun sane(input: List<String>): Pair<Any, Any> {
    var index = 0
    val cals = mutableListOf<Int>()
    input.forEach {
        if (it.isBlank()) {
            index++
        } else {
            val num = it.toInt()
            if (cals.size > index) {
                cals[index] += num
            } else {
                cals.add(num)
            }
        }
    }
    cals.sort()
    return cals.last() to cals.takeLast(3).sum()
}
