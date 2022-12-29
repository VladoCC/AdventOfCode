package d14

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
    val forms = Array(input.size) {
        val split = input[it].split(" -> ")
        Array(split.size) {
            val p = split[it].split(",")
            p[0].toInt() to p[1].toInt()
        }
    }
    val lowest = forms.flatten().maxOf { it.second }
    val left = forms.flatten().minOf { it.first }
    val right = forms.flatten().maxOf { it.first }
    val map = mutableMapOf<Pair<Int, Int>, Boolean>()
    forms.forEach {
        it.toList().windowed(2).forEach {
            if (it.size == 2) {
                val f = it[0]
                val s = it[1]
                if (f.first == s.first) {
                    val l = minOf(f.second, s.second)
                    val m = maxOf(f.second, s.second)
                    (l..m).forEach {
                        map[it to (f.first - left)] = true
                    }
                } else {
                    val l = minOf(f.first, s.first)
                    val m = maxOf(f.first, s.first)
                    (l..m).forEach {
                        map[f.second to (it - left)] = true
                    }
                }
            }
        }
    }

    var sand = 500 - left to 0
    var count = 0
    while (true) {
        if (sand.second + 1 < lowest + 2) {
            if (map[(sand.second + 1) to sand.first] != true) {
                sand = sand.copy(second = sand.second + 1)
                continue
            }
                if (map[(sand.second + 1) to (sand.first - 1)] != true) {
                    sand = (sand.first - 1) to (sand.second + 1)
                    continue
                }

                if (map[(sand.second + 1) to (sand.first + 1)] != true) {
                    sand = (sand.first + 1) to (sand.second + 1)
                    continue
                }

        }
        count++
        map[sand.second to sand.first] = true
        if (sand == 500 - left to 0) {
             break
        }
        sand = 500 - left to 0
    }
    return count to 0
}
