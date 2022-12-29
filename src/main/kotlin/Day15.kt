package d15

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
    val sensors = mutableListOf<Sensor>()
    val beacons = mutableSetOf<Pair<Int, Int>>()
    input.forEach {
        val split = it.split(":")
        val sY = split[0].takeLastWhile { it.isDigit() || it == '-' }
        val sX = split[0].dropLast(sY.length + 4).takeLastWhile { it.isDigit() || it == '-'  }
        val bY = split[1].takeLastWhile { it.isDigit() || it == '-'  }
        val bX = split[1].dropLast(bY.length + 4).takeLastWhile { it.isDigit() || it == '-'  }
        val b = bX.toInt() to bY.toInt()
        beacons.add(b)
        sensors.add(Sensor(sX.toInt() to sY.toInt(), b))
    }
    val ty = 2000000
    val cantBe = sensors.map {
        val dist = it.dist()
        if (ty in (it.pos.second - dist..it.pos.second + dist)) {
            val res = (dist - abs(ty - it.pos.second))
            (-res..res).map { n -> it.pos.first + n }
        } else {
            emptySet()
        }
    }.reduce { acc, longs -> acc.union(longs) } - beacons.filter { it.second == ty }.map { it.first }.toSet()

    println("Start")
    val size = 4000000
    val lines = MutableList<MutableList<IntRange>>(size = size + 1) {
        mutableListOf(0..size )
    }
    fun draw() {
        (0..size).forEach { l ->
            println((0..size).map { if (lines[l].any { r -> r.contains(it) }) '.' else '#' }.joinToString(""))
        }
        println("-------------------------------------------")
    }
    sensors.forEach {
        val dist = it.dist()
        (-dist..dist).forEach { l ->
            val line = it.pos.second + l
            if (line in (0..size)) {
                val a = dist - abs(l)
                val p = (it.pos.first - a) to (it.pos.first + a)
                val prev = lines[line]
                val new = mutableListOf<IntRange>()
                prev.forEach {
                    if (p.second < it.first || p.first > it.last) {
                        new.add(it)
                    } else if (p.first > it.first) {
                        if (p.second < it.last) {
                            new.add(it.first until p.first)
                            new.add(p.second + 1..it.last)
                        } else {
                            new.add(it.first until p.first)
                        }
                    } else if (p.second < it.last) {
                        new.add(p.second + 1.. it.last)
                    }
                }
                lines[line] = new
            }
        }
        //draw()
    }

    val freq = lines.mapIndexed { index, intRanges -> index to intRanges }.filter { it.second.isNotEmpty() }.first()

    return cantBe.size to freq.first + freq.second.first().first * 4000000L
}
data class Sensor(val pos: Pair<Int, Int>, val beacon: Pair<Int, Int>) {
    fun dist(): Int {
        return abs(pos.first - beacon.first) + abs(pos.second - beacon.second)
    }
}