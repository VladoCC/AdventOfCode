package d13

import measure
import printResult
import java.io.File
import java.util.function.BiPredicate
import kotlin.math.abs
import kotlin.math.floor

fun main(args: Array<String>) {
    val lines = File("input.txt").readLines()
    printResult(sane(lines))
    //measure { sane(lines) }
}

fun functional(input: List<String>): Pair<Any, Any> {
    val ring: (Int) -> Int = { if (it < 1) 3 else if (it > 3) 1 else it  }
    val value: (Char, Char) -> Int = { it, from -> it.minus(from).plus(1) }
    fun choice(op: Char): Array<Int> = value(op, 'A').let {
        arrayOf(ring(it - 1), it, ring(it + 1))
    }
    return input.sumOf { input -> value(input[2], 'X').let { choice(input[0]).indexOf(it) * 3 + it } } to
            input.sumOf { input -> value(input[2], 'X').let { choice(input[0])[it - 1] + (it - 1) * 3 } }
}

fun sane(input: List<String>): Pair<Any, Any> {
    fun toList(string: String): List<Any> {
        val res = mutableListOf<Any>()
        var content = string.drop(1).dropLast(1)
        while (content.isNotEmpty()) {
            if (content[0].isDigit()) {
                val num = content.takeWhile { it.isDigit() }
                res.add(num.toInt())
                content = content.drop(num.length + 1)
            } else {
                var brackets = 0
                val list = content.takeWhile {
                    if (it == '[') {
                        brackets++
                    } else if (it == ']') {
                        brackets--
                    }
                    brackets != 0
                } + "]"
                res.add(toList(list))
                content = content.drop(list.length + 1)
            }
        }
        return res
    }

    fun isCorrect(left: Any, right: Any): Int {
        var result = false
        if (left is Int && right is Int) {
            return right - left
        }
        if (left is Int) {
            return isCorrect(listOf(left), right)
        }
        if (right is Int) {
            return isCorrect(left, listOf(right))
        }
        if (left is List<*> && right is List<*>) {
            if (left.isEmpty() && right.isEmpty()) {
                return 0
            }
            if (left.isEmpty()) {
                return 1
            }
            if (right.isEmpty()) {
                return -1
            }
            val check = isCorrect(left.first()!!, right.first()!!)
            if (check != 0) {
                return check
            }
            return isCorrect(left.drop(1), right.drop(1))
        }
        throw IllegalArgumentException()
    }

    val packets = input.chunked(3).map {
        val left = it[0]
        val right = it[1]
        toList(left) to toList(right)
    }
    val correct = packets.mapIndexed { index, pair ->
        if (isCorrect(pair.first, pair.second) > 0) {
            index + 1
        } else {
            0
        }
    }.sum()
    val signal = input.filter { it.isNotBlank() }.plus("[[2]]").plus("[[6]]")
        .map { toList(it) }
        .sortedWith { o1, o2 -> isCorrect(o1, o2) }.asReversed()
    signal.forEachIndexed { index, anies ->
        println("${index + 1}. $anies")
    }
    return correct to signal
}
