package d9

import measure
import printResult
import java.io.File
import java.util.function.BiPredicate
import kotlin.math.abs

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
    val knotCount = 10
    var knots = (1..knotCount).map { 0 to 0 }

    val set = mutableSetOf<Pair<Int, Int>>()
    set.add(knots.last())
    fun <T> transform(original: List<T>, result: List<T>, f: (List<T>, List<T>, Int) -> T): List<T> {
        if (result.size == original.size) {
            return result
        }
        return transform(original, result + f(original, result, result.size), f)
    }
    fun move(dir: Pair<Int, Int>, count: Int, knots: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        val result = transform(knots, emptyList()) { original, current, it ->
            if (it == 0) {
                val cur = original[it]
                (cur.first + dir.first) to (cur.second + dir.second)
            } else {
                val first = current[it - 1]
                val second = original[it]
                if (abs(first.first - second.first) > 1 || abs(first.second - second.second) > 1) {
                    val diff = first.first - second.first to first.second - second.second
                    val absDiff =
                        (if (diff.first != 0) diff.first / abs(diff.first) else 0) to (if (diff.second != 0) diff.second / abs(
                            diff.second
                        ) else 0)
                    second.first + absDiff.first to second.second + absDiff.second
                } else second
            }
        }
        set.add(result.last())
        if (count > 1) {
            return move(dir, count - 1, result)
        } else {
            return result
        }
    }

    val moves = input.map {
        val dir = when(it[0]) {
            'R' -> 0 to 1
            'L' -> 0 to -1
            'U' -> 1 to 0
            'D' -> -1 to 0
            else -> 0 to 0
        }
        dir to it.drop(2).toInt()
    }.forEach { (dir, count) ->
        knots = move(dir, count, knots)
    }
    return set.size to 0
}