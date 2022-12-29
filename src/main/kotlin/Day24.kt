package d24

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
    val obstacles = input.mapIndexed { row, s ->
        s.mapIndexed { col, c ->
            when (c) {
                'v' -> Obstacle(Dir.Bottom, col to row)
                '^' -> Obstacle(Dir.Top, col to row)
                '<' -> Obstacle(Dir.Left, col to row)
                else -> Obstacle(Dir.Right, col to row)
            }
        }
    }.flatten()
    val states = mutableListOf((1 to 0) to obstacles)
    val res: Pair<Pair<Int, Int>, List<Obstacle>>
    while (states.isNotEmpty()) {
        val f = states.first()
        println(f.first)
        if (f.first == input[0].length - 2 to input.size - 2) {
            res = f.copy(f.first.copy(second = f.first.second + 1))
            break
        }

        val list = f.second.map { it.move(input[0].length, input.size) }
        val set = list.map { it.pos }.toSet()

        if (f.first == 1 to 0) {
            states.add(f.first to list)
            val bottom = f.first.copy(second = f.first.second + 1)
            if (bottom !in set) {
                states.add(bottom to list)
            }
        } else {
            if (f.first.first > 1) {
                val left = f.first.copy(first = f.first.first - 1)
                if (left !in set) {
                    states.add(left to list)
                }
            }
            if (f.first.first < input[0].length - 2) {
                val right = f.first.copy(first = f.first.first + 1)
                if (right !in set) {
                    states.add(right to list)
                }
            }
            if (f.first.second > 1) {
                val top = f.first.copy(second = f.first.second - 1)
                if (top !in set) {
                    states.add(top to list)
                }
            }
            if (f.first.second < input.size - 2) {
                val bottom = f.first.copy(second = f.first.second + 1)
                if (bottom !in set) {
                    states.add(bottom to list)
                }
            }
            if (f.first !in set) {
                states.add(f.first to list)
            }
            if (f.first == 1 to 1) {
                val top = f.first.copy(second = f.first.second - 1)
                states.add(top to list)
            }
        }

        states.removeAt(0)
    }
    return 0 to 0
}
data class Obstacle(val dir: Dir, val pos: Pair<Int, Int>) {
    fun move(width: Int, height: Int): Obstacle {
        val new = pos.first + dir.move.first to pos.second + dir.move.second
        val next = if (new.first == 0) {
            new.copy(first = width - 2)
        } else if (new.first == width - 1) {
            new.copy(first = 1)
        } else if (new.second == 0) {
            new.copy(second = height - 2)
        } else if (new.second == height - 1) {
            new.copy(second = 1)
        } else {
            new
        }
        return copy(pos = next)
    }
}
enum class Dir(val move: Pair<Int, Int>) {
    Left(-1 to 0), Right(1 to 0), Top(0 to -1), Bottom(0 to 1)
}