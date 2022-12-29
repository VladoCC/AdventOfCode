package d21

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
    val monkeys = input.map {
        val split = it.split(": ")
        return@map if (split[0] == "humn") {
            split[0] to 0L
        } else if (split[1].first().isDigit()) {
            split[0] to split[1].toLong()
        } else {
            split[0] to Task(split[1].take(4), split[1].takeLast(4), split[1].drop(5).first())
        }
    }.toMap()
    fun result(task: Any): Long {
        return if (task is Long) {
            task
        } else {
            task as Task
            val first = result(monkeys[task.first]!!)
            val second = result(monkeys[task.second]!!)

            when (task.op) {
                '+' -> first + second
                '*' -> first * second
                '-' -> first - second
                else -> first / second
            }
        }
    }

    fun contains(task: Any): Boolean {
        if (task is Long) {
            return false
        } else {
            task as Task
            if (task.first == "humn" || task.second == "humn") {
                return true
            }
            return contains(monkeys[task.first]!!) || contains(monkeys[task.second]!!)
        }
    }

    fun find(name: String, result: Long): Long {
        if (name == "humn") return result
        val task = monkeys[name]!! as Task
        val left = task.first == "humn" || contains(monkeys[task.first]!!)
        val other = if (left) result(monkeys[task.second]!!) else result(monkeys[task.first]!!)
        val cur = if (left) task.first else task.second
        return when (task.op) {
            '+' -> find(cur, result - other)
            '*' -> find(cur, result / other)
            '-' -> {
                if (left) {
                    find(cur, result + other)
                } else {
                    find(cur, other - result)
                }
            }
            else -> {
                if (left) {
                    find(cur, result * other)
                } else {
                    find(cur, other / result)
                }
            }
        }
    }

    val res = with(monkeys["root"]!! as Task) {
        val left = contains(monkeys[this.first]!!)
        println(left)
        val other = if (left) result(monkeys[this.second]!!) else result(monkeys[this.first]!!)
        val cur = if (left) this.first else this.second
        find(cur, other)
    }

    return result(monkeys["root"]!!) to res
}
class Task(val first: String, val second: String, val op: Char)