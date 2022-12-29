package d3

import measure
import printResult
import java.io.File

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
    val ord: (Char) -> Int = { if (it in 'a'..'z') it - 'a' + 1 else it - 'A' + 27 }
    return input.map { it.take(it.length / 2).toSet().intersect(it.takeLast(it.length / 2).toSet()) }
        .map { it.toCharArray() }.reduce { a, b -> a + b }
        .sumOf(ord) to
            input.windowed(3, 3).map {
                it.map { it.toSet() }.reduce { acc, s ->
                    acc.intersect(s)
                }
            }.map { it.first() }
                .sumOf(ord)
}