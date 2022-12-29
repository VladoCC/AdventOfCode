package d5

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
    val crates = input.take(8)
    val stacks = (0..8).map { i ->
        (7 downTo 0).map { j ->
            crates[j].drop(i * 4).drop(1).take(1)
        }.filter { it.isNotBlank() }.toMutableList()
    }

    val mapping = input.drop(10).map {
        val count = it.drop(5).takeWhile { it.isDigit() }.toInt()
        val from = it.dropLast(5).takeLast(1).toInt() - 1
        val to = it.takeLast(1).toInt() - 1
        Triple(count, from, to)
    }

    val s1 = stacks.map { it.toMutableList() }
    mapping.forEach { (count, from, to) ->
        (1..count).forEach {
            val v = s1[from].last()
            s1[to].add(v)
            s1[from].removeLast()
        }
    }

    val s2 = stacks.map { it.toMutableList() }
    mapping.forEach { (count, from, to) ->
        val v = s2[from].takeLast(count)
        s2[to].addAll(v)
        (1..count).forEach {
            s2[from].removeLast()
        }
    }

    return s1.map { it.last() }.joinToString("") to s2.map { it.last() }.joinToString("")
}