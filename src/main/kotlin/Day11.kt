package d11

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
    val monkeyList = input.chunked(7).map {
        val a = it[2].drop(25)
        val arg: Long
        val op: Char
        if (a == "old") {
            arg = 2
            op = '^'
        } else {
            op = it[2].drop(23).first()
            arg = a.toLong()
        }
        Monkey(
            it[0].dropLast(1).takeLast(1).toInt(),
            it[1].drop(18).split(", ").map { it.toLong() }.toMutableList(),
            op,
            arg,
            it[3].drop(21).toLong(),
            it[4].drop(29).toInt() to it[5].drop(30).toInt()
        )
    }
    (1..10000).forEach {
        monkeyList.forEach {
            it.inspect()
            it.items.indices.forEach { item ->
                val next = if (it.items[item] % it.div == 0L) {
                    it.actions.first
                } else {
                    it.actions.second
                }
                monkeyList[next].items.add(it.items[item])
            }
            it.items.clear()
        }
    }
    return monkeyList.sortedBy { it.inspected }.map { it.inspected }.takeLast(2).reduce { acc, l -> acc * l } to 0
}
data class Monkey(val index: Int, val items: MutableList<Long>, val op: Char, val arg: Long, val div: Long, val actions: Pair<Int, Int>, var inspected: Long = 0) {

    fun inspect() {
        items.indices.forEach {
            val new = if (op == '*') items[it] * arg else if (op == '^') items[it] * items[it] else items[it] + arg
            items[it] = new % (2 * 3 * 5 * 7 * 11 * 13 * 17 * 19)
            inspected++
        }
    }
}
