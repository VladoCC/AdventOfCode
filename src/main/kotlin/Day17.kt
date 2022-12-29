package d17

import measure
import printResult
import java.io.File
import java.util.function.BiPredicate
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

fun main(args: Array<String>) {
    val lines = File("input.txt").readLines()
    printResult(original(lines))
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
    val rocks = Array<Array<Array<Boolean>>>(5) {
        if (it == 0) {
            Array(1) {
                Array(4) {
                    true
                }
            }
        } else if (it == 1) {
            Array(3) { col ->
                Array(3) { row ->
                    abs(col - 1) + abs(row - 1) < 2
                }
            }
        } else if (it == 2) {
            Array(3) { col ->
                Array(3) { row ->
                    col == 0 || row == 2
                }
            }
        } else if (it == 3) {
            Array(4) { col ->
                Array(1) { row ->
                    true
                }
            }
        } else {
            Array(2) { col ->
                Array(2) { row ->
                    true
                }
            }
        }
    }
    rocks.forEach {
        println(it.joinToString("\n") { it.joinToString("") { if (it) "#" else "." } })
    }
    val cave = mutableListOf<MutableList<Boolean>>((0..8).map { true }.toMutableList())
    var highest = 0L
    var jet = 0
    var rock = 0
    var current = rocks[rock]
    var pos = 3 to (highest + 4)
    var start = 0L

    fun test(pos: Pair<Int, Long>, shape: Array<Array<Boolean>>): Boolean {
        (0 until shape[0].size).forEach {
            if (shape[0][it] && cave[(pos.second - start).toInt()][pos.first + it]) {
                return true
            }
        }
        (0 until shape.size).forEach {
            if (shape[it][0] && cave[(pos.second + it - start).toInt()][pos.first]) {
                return true
            }
            if (shape[it].last() && cave[(pos.second + it - start).toInt()][pos.first + shape[it].size - 1]) {
                return true
            }
        }
        return false
    }

    fun draw() {
        cave.asReversed().forEach {
            println(it.map { if (it) '#' else '.' }.joinToString(" "))
        }
        println("----------------------------------------")
    }
    fun check(coords: Pair<Int, Int>): Boolean {
        return cave[coords.second][coords.first]
    }
    fun find(): Int {
        return cave.mapIndexed { index, booleans -> index to booleans }.filter { it.second.all { it } }.maxOf { it.first }
    }

    (0 until 1000000000000).forEach {
        println(it)
        (cave.size + start..(pos.second + current.size - 1)).forEach {
            cave.add(mutableListOf(true, false, false, false, false, false, false, false, true))
        }
        while (true) {
            val dir = input[0][jet]
            jet++
            if (jet == input[0].length) {
                jet = 0
            }
            val add = if (dir == '<') {
                -1
            } else {
                1
            }
            val new = pos.copy(first = pos.first + add)
            val col = test(new, current)
            if (!col) {
                pos = new
            }
            val down = pos.copy(second = pos.second - 1)
            if (test(down, current)) {
                break
            } else {
                pos = down
            }
        }
        current.forEachIndexed { row, booleans ->
            booleans.forEachIndexed { col, b ->
                if (b) {
                    cave[(pos.second + row - start).toInt()][pos.first + col] = true
                }
            }
        }
        val prev = start
        start = max(find() + start, start)
        (0 until start - prev).forEach {
            cave.removeAt(0)
        }
        //println("Start: $start")
        //draw()
        highest = max(pos.second + current.size - 1, highest)
        rock++
        if (rock == rocks.size) {
            rock = 0
        }
        current = rocks[rock]
        if (rock == 0 && jet == 0) {
            println("Cycle: $it")
        }
        pos = 3 to (highest + 4)
    }
    return highest to 0
}

fun original(input: List<String>): Pair<Any, Any> {
    val rocks = Array<Array<Array<Boolean>>>(5) {
        if (it == 0) {
            Array(1) {
                Array(4) {
                    true
                }
            }
        } else if (it == 1) {
            Array(3) { col ->
                Array(3) { row ->
                    abs(col - 1) + abs(row - 1) < 2
                }
            }
        } else if (it == 2) {
            Array(3) { col ->
                Array(3) { row ->
                    col == 0 || row == 2
                }
            }
        } else if (it == 3) {
            Array(4) { col ->
                Array(1) { row ->
                    true
                }
            }
        } else {
            Array(2) { col ->
                Array(2) { row ->
                    true
                }
            }
        }
    }
    rocks.forEach {
        println(it.joinToString("\n") { it.joinToString("") { if (it) "#" else "." } })
    }
    val cave = mutableListOf<MutableList<Boolean>>((0..8).map { true }.toMutableList())
    var highest = 0
    var jet = 0
    var rock = 0
    var current = rocks[rock]
    var pos = 3 to (highest + 4)

    fun test(pos: Pair<Int, Int>, shape: Array<Array<Boolean>>): Boolean {
        (0 until shape[0].size).forEach {
            if (shape[0][it] && cave[pos.second][pos.first + it]) {
                return true
            }
        }
        (0 until shape.size).forEach {
            if (shape[it][0] && cave[pos.second + it][pos.first]) {
                return true
            }
            if (shape[it].last() && cave[pos.second + it][pos.first + shape[it].size - 1]) {
                return true
            }
        }
        return false
    }

    fun draw() {
        cave.asReversed().forEach {
            println(it.map { if (it) '#' else '.' }.joinToString(" "))
        }
        println("----------------------------------------")
    }

    (0 until 2022).forEach {
        println(it)
        (cave.size..(pos.second + current.size - 1)).forEach {
            cave.add(mutableListOf(true, false, false, false, false, false, false, false, true))
        }
        while (true) {
            val dir = input[0][jet]
            jet++
            if (jet == input[0].length) {
                jet = 0
            }
            val add = if (dir == '<') {
                -1
            } else {
                1
            }
            val new = pos.copy(first = pos.first + add)
            val col = test(new, current)
            if (!col) {
                pos = new
            }
            val down = pos.copy(second = pos.second - 1)
            if (test(down, current)) {
                break
            } else {
                pos = down
            }
        }
        current.forEachIndexed { row, booleans ->
            booleans.forEachIndexed { col, b ->
                if (b) {
                    cave[pos.second + row][pos.first + col] = true
                }
            }
        }
        //draw()
        highest = max(pos.second + current.size - 1, highest)
        rock++
        if (rock == rocks.size) {
            rock = 0
        }
        current = rocks[rock]
        pos = 3 to (highest + 4)
    }
    return highest to 0
}