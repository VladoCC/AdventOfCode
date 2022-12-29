package d16

import measure
import printResult
import java.io.File
import java.util.function.BiPredicate
import kotlin.math.abs
import kotlin.math.floor

fun main(args: Array<String>) {
    val lines = File("input1.txt").readLines()
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
    val nodes = input.map {
        val split = it.split("; ")
        val rate = split[0].split("=")[1].toInt()
        val name = split[0].drop(6).take(2)
        name to Node(rate, name, split[1].drop(if (split[1].length == 24) 22 else 23).split(", "))
    }.toMap()
    nodes.forEach { t, u ->
        u.neighNames.forEach {
            // println(it)
            u.neighbors.add(nodes[it]!!)
        }
        u.neighbors.sortBy { -it.rate }
    }

    val options = mutableListOf<Path>(Path(listOf(nodes["AA"]!!)))
    val finished = mutableListOf<Path>()
    while (options.isNotEmpty()) {
        val f = options.first()
        val fCost = f.round
        if (fCost < 29) {
            var added = false
            f.nodes.last().neighbors.forEach {
                val new = f.with(it)
                if (new.isNotEmpty()) {
                    added = true
                }
                options.addAll(new)
            }
            if (!added) {
                finished.add(f)
            }
        } else {
            finished.add(f)
        }
        options.removeAt(0)
    }

    finished.forEach {
        println(it)
    }

    return finished.maxBy { it.rate } to 0
}

data class Node(val rate: Int, val name: String, val neighNames: List<String>) {
    val neighbors: MutableList<Node> = mutableListOf()
}
data class Path(val nodes: List<Node>, val locked: Set<Node> = mutableSetOf(), var rate: Int = 0, var round: Int = 0) {
    val set = nodes.toMutableSet()

    fun with(node: Node): List<Path> {
        return if (node in locked || round == 30) {
            emptyList()
        } else if (node in set) {
            val i = nodes.indexOf(node)
            listOf(Path(nodes + node, locked.union(nodes.drop(i + 1)), rate, round + 1))
        } else {
            val without = Path(nodes + node, locked, rate, round + 1)
            if (round > 28) {
                listOf(without)
            } else {
                val with = Path(nodes + node, locked, rate + node.rate * (28 - round), round + 2)
                listOf(without, with)
            }
        }
    }
}