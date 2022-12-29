package d8

import measure
import printResult
import java.io.File
import java.util.function.BiPredicate

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
    val size = input.size
    val grid = Array<Array<Int>>(size) { row ->
        val line = input[row]
        Array(size) {
            line[it].toString().toInt()
        }
    }
    val lToR = grid.indices.map { row ->
        var h = -1
        val set = mutableSetOf<Pair<Int, Int>>()
        (0..grid.size - 1).forEach { col ->
            val v = grid[row][col]
            if (v > h) {
                set.add(row to col)
                h = v
            }
        }
        return@map set
    }.map { it.toSet() }.reduce { a, b -> a.union(b) }
    val rToL = grid.indices.map { row ->
        var h = -1
        val set = mutableSetOf<Pair<Int, Int>>()
        (grid.size - 1 downTo 0).forEach { col ->
            val v = grid[row][col]
            if (v > h) {
                set.add(row to col)
                h = v
            }
        }
        return@map set
    }.map { it.toSet() }.reduce { a, b -> a.union(b) }
    val tToB = grid.indices.map { col ->
        var h = -1
        val set = mutableSetOf<Pair<Int, Int>>()
        (0..grid.size - 1).forEach { row ->
            val v = grid[row][col]
            if (v > h) {
                set.add(row to col)
                h = v
            }
        }
        return@map set
    }.map { it.toSet() }.reduce { a, b -> a.union(b) }
    val bToT = grid.indices.map { col ->
        var h = -1
        val set = mutableSetOf<Pair<Int, Int>>()
        (grid.size - 1 downTo 0).forEach { row ->
            val v = grid[row][col]
            if (v > h) {
                set.add(row to col)
                h = v
            }
        }
        return@map set
    }.map { it.toSet() }.reduce { a, b -> a.union(b) }
    val first = lToR.union(rToL).union(tToB).union(bToT).size

    fun <T> Iterable<T>.takeInclusive(predicate: (T) -> Boolean): List<T> {
        val res = mutableListOf<T>()
        forEach {
            res.add(it)
            if (!predicate(it)) {
               return res
            }
        }
        return res
    }

    val scores = mutableListOf<Int>()
    (0 until grid.size).forEach {row ->
        (0 until grid.size).forEach { col ->
            val cur = grid[row][col]
            val l = (col - 1 downTo 0).takeInclusive { grid[row][it] < cur }.size
            val r = (col + 1 until grid.size).takeInclusive { grid[row][it] < cur }.size
            val t = (row - 1 downTo 0).takeInclusive { grid[it][col] < cur }.size
            val b = (row + 1 until grid.size).takeInclusive { grid[it][col] < cur }.size
            scores.add(l * r * t * b)
        }
    }

    return first to scores.max()
}