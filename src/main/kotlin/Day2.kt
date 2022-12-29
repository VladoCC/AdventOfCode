package d2

import measure
import printResult
import java.io.File

fun main(args: Array<String>) {
    val lines = File("input.txt").readLines()
    printResult(functional(lines))
    measure { functional(lines) }
    printResult(sane(lines))
    measure { sane(lines) }
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
    val scores = input.map {
        val op = it[0] - 'A'
        val you = it[2] - 'X'
        val score = if (op == you)
            3
        else {
           if (you == 0) {
               if (op == 1) {
                   0
               } else {
                   6
               }
           } else if (you == 1) {
               if (op == 0) {
                   6
               } else {
                   0
               }
           } else {
               if (op == 1) {
                   6
               } else {
                   0
               }
           }
        }
        score + you + 1
    }
    val scores2 = input.map {
        val op = it[0] - 'A'
        val you = it[2] - 'X'
        val score = if (you == 0) {
            if (op == 0) {
                3
            } else {
                op
            }
        } else if (you == 1) {
            op + 1
        } else {
            if (op == 2) {
                1
            } else {
                op + 2
            }
        }
        you * 3 + score
    }
    return scores.sum() to scores2.sum()
}