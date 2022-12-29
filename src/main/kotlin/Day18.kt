package d18

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
    val triples = input.map {
        val split = it.split(",")
        Triple<Int, Int, Int>(split[0].toInt(), split[1].toInt(), split[2].toInt())
    }
    val x = triples.maxOf { it.first }
    val y = triples.maxOf { it.second }
    val z = triples.maxOf { it.third }
    val map = Array(x + 1) {
        Array(y + 1) {
            Array(z + 1) {
                false
            }
        }
    }
    triples.forEach {
        map[it.first][it.second][it.third] = true
    }

    fun get(coord: Triple<Int, Int, Int>): Boolean {
        if (coord.first < 0 || coord.second < 0 || coord.third < 0) {
            return false
        }
        if (coord.first > x || coord.second > y || coord.third > z) {
            return false
        }
        return map[coord.first][coord.second][coord.third]
    }

    val res = triples.map {
        listOf(
            get(it.copy(first = it.first - 1)),
            get(it.copy(first = it.first + 1)),
            get(it.copy(second = it.second - 1)),
            get(it.copy(second = it.second + 1)),
            get(it.copy(third = it.third - 1)),
            get(it.copy(third = it.third + 1)),
        ).filter { !it }.size
    }.sum()

    val check = mutableListOf(Triple(-1, -1, -1))
    val air = mutableListOf<Triple<Int, Int, Int>>(Triple(-1, -1, -1))
    while (check.isNotEmpty()) {
        val f = check.first()
        println(f)

        val xM = f.copy(first = f.first - 1)
        if (xM.first > - 2 && !get(xM) && xM !in air) {
            check.add(xM)
            air.add(xM)
        }

        val xP = f.copy(first = f.first + 1)
        if (xP.first < 21 && !get(xP) && xP !in air) {
            check.add(xP)
            air.add(xP)
        }

        val yM = f.copy(second = f.second - 1)
        if (yM.second > - 2 && !get(yM) && yM !in air) {
            check.add(yM)
            air.add(yM)
        }

        val yP = f.copy(second = f.second + 1)
        if (yP.second < 21 && !get(yP) && yP !in air) {
            check.add(yP)
            air.add(yP)
        }

        val zM = f.copy(third = f.third - 1)
        if (zM.third > - 2 && !get(zM) && zM !in air) {
            check.add(zM)
            air.add(zM)
        }

        val zP = f.copy(third = f.third + 1)
        if (zP.third < 21 && !get(zP) && zP !in air) {
            check.add(zP)
            air.add(zP)
        }

        check.removeAt(0)
    }

    val outer = air.map {
        listOf(
            it.copy(first = it.first - 1).let { get(it) to it },
            it.copy(first = it.first + 1).let { get(it) to it },
            it.copy(second = it.second - 1).let { get(it) to it },
            it.copy(second = it.second + 1).let { get(it) to it },
            it.copy(third = it.third - 1).let { get(it) to it },
            it.copy(third = it.third + 1).let { get(it) to it },
        ).filter { it.first }.map { it.second }.toSet()
    }.reduce { acc, triples -> acc.union(triples) }.map {
        listOf(
            it.copy(first = it.first - 1),
            it.copy(first = it.first + 1),
            it.copy(second = it.second - 1),
            it.copy(second = it.second + 1),
            it.copy(third = it.third - 1),
            it.copy(third = it.third + 1),
        ).filter { it in air }.size
    }.sum()

    return res to outer
}
