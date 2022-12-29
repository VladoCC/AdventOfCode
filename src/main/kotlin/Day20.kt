package d20

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
    println(Day20(lines.map { it.toInt() }).runPartTwo())
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
    val original = input.mapIndexed { ind, it -> Wrapper(it.toInt() % input.size, it.toLong() * 811589153, ind) }
    val moved = original.toMutableList()
    (0 until 10).forEach {
        original.indices.forEach {
            //println(moved.map { it.move })
            val cur = original[it]
            if (cur.zero != 0L) {
                val pos = moved.indexOfFirst { it.index == cur.index }
                moved.removeAt(pos)
                moved.add((pos + cur.zero).mod(moved.size), cur)
            }
        }
    }
    val zero = moved.indexOfFirst { it.zero == 0L }
    val res = arrayOf(1000, 2000, 3000).map {
        moved[(it + zero) % original.size].zero
    }
    return res.sum() to 0
}
class Wrapper(val move: Int, val zero: Long, val index: Int)

class Day20(val input: List<Int>) {

    fun runPartOne(): Long = decrypt()

    fun runPartTwo(): Long = decrypt(811_589_153, 10)

    private fun decrypt(decryptKey: Int = 1, mixTimes: Int = 1): Long {
        val original = input.mapIndexed { index, i -> Pair(index, i.toLong() * decryptKey) }
        val moved = original.toMutableList()
        repeat(mixTimes) {
            original.forEach { p ->
                val idx = moved.indexOf(p)
                moved.removeAt(idx)
                moved.add((idx + p.second).mod(moved.size), p)
            }
        }
        return moved.map { it.second }.let {
            val idx0 = it.indexOf(0)
            it[(1000 + idx0) % moved.size] + it[(2000 + idx0) % moved.size] + it[(3000 + idx0) % moved.size]
        }
    }
}