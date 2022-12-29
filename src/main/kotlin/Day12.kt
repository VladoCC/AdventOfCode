package d12

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
    val h = input.size
    val w = input[0].length
    var start: Pair<Int, Int> = 0 to 0
    var end: Pair<Int, Int> = 0 to 0
    val grid = Array(h) { row ->
        Array(w) { col ->
            val c = input[row][col]
            if (c == 'S') {
                start = row to col
                Node(0, col, row)
            } else if (c == 'E') {
                end = row to col
                Node('z' - 'a', col, row)
            } else {
                Node(c - 'a', col, row)
            }
        }
    }

    (0 until h).forEach { row ->
        (0 until w).forEach { col ->
            val node = grid[row][col]
            if (row > 0) {
                node.neighbors.add(grid[row - 1][col])
            }
            if (row < h - 1) {
                node.neighbors.add(grid[row + 1][col])
            }
            if (col > 0) {
                node.neighbors.add(grid[row][col - 1])
            }
            if (col < w - 1) {
                node.neighbors.add(grid[row][col + 1])
            }
        }
    }

    fun bfs(start: Pair<Int, Int>): List<Node>? {
        val visited = mutableListOf(start.let { grid[it.first][it.second] })
        val queue = mutableListOf(start.let { listOf(grid[it.first][it.second]) })
        val finish = end.let { grid[it.first][it.second] }
        while (queue.isNotEmpty()) {
            val cur = queue.first()
            var found = false
            cur.last().neighbors.forEach {
                if (it.height <= cur.last().height + 1 && it !in visited) {
                    queue.add(cur + it)
                    visited.add(it)
                    if (it == finish) {
                        found = true
                        return@forEach
                    }
                }
            }
            if (found) {
                println("found")
                break
            }
            queue.removeAt(0)
        }
        return queue.lastOrNull()
    }
    val aStarts = grid.map { it.filter { it.height == 0 } }.flatten()

    return bfs(start)!!.size - 1 to aStarts.map { bfs(it.y to it.x) }.filterNotNull().minOf { it.size }
}
data class Node(val height: Int, val x: Int, val y: Int) {
    val neighbors: MutableList<Node> = mutableListOf()
}
