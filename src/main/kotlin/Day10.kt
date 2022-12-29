package d10

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
    val values = input.fold(listOf(1)) { acc, cur ->
        if (cur.startsWith("addx")) {
            acc + listOf(acc.last(), acc.last() + cur.drop(5).toInt())
        } else {
            acc + listOf(acc.last())
        }
    }
    val acc = values.drop(19).chunked(40).mapIndexed { index, ints -> ints.first() * ((index) * 40 + 20) }

    val text = "\n" + values.mapIndexed { index, i ->
        val ind = index % 40
        if ((i) in (ind - 1..ind + 1)) {
            '#'
        } else {
            '.'
        }
    }.chunked(40).joinToString("\n") { it.joinToString("") }
    return acc.sum() to text
}
