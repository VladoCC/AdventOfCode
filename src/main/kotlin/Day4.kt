package d4

import measure
import printResult
import java.io.File

fun main(args: Array<String>) {
    val lines = File("input.txt").readLines()
    printResult(sane(lines))
    measure { sane(lines) }
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
    val pair: (String) -> Pair<Int, Int> = {
        val l1 = it.takeWhile { it.isDigit() }.toInt()
        val r1 = it.dropWhile { it != '-' }.drop(1).takeWhile { it.isDigit() }.toInt()
        l1 to r1
    }
    val contains: (Pair<Int, Int>, Pair<Int, Int>) -> Boolean = { p1, p2 ->
        p1.first <= p2.first && p1.second >= p2.second
    }
    val overlaps: (Pair<Int, Int>, Pair<Int, Int>) -> Boolean = { p1, p2 ->
        contains(p1, p2) || (p2.first <= p1.second && p1.first <= p2.second)
    }
    val find: (List<String>, (Pair<Int, Int>, Pair<Int, Int>) -> Boolean) -> Int = { it, f ->
        input.map {
            val split = it.split(",")
            pair(split[0]) to pair(split[1])
        }.count {
            f(it.first, it.second) || f(it.second, it.first)
        }
    }
    return find(input, contains) to find(input, overlaps)
}