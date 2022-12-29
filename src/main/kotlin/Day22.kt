package d22

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
    val len = input.dropLast(2).maxOf { it.length }
    val cells = input.dropLast(2).mapIndexed { row, it ->
        val full = it + (it.length until len).map { " " }.joinToString("")
        full.mapIndexed { col, it ->
            Cell(when (it) {
                '.' -> Type.Open
                '#' -> Type.Closed
                else -> Type.Empty
            }, col + 1, row + 1)
        }
    }
    fun get(x: Int, y: Int): Cell {
        if (x < 0 || x >= len) {
            return Cell(Type.Empty, 0, 0)
        }
        if (y < 0 || y >= input.size - 2) {
            return Cell(Type.Empty, 0, 0)
        }
        return cells[y][x]
    }
    cells.forEachIndexed { row, line ->
        line.forEachIndexed { col, cell ->
            if (cell.type != Type.Empty) {
                var l = get(col - 1, row)
                if (l.type == Type.Empty) {
                    l = line.asReversed().first { it.type != Type.Empty }
                }
                cell.left = l

                var r = get(col + 1, row)
                if (r.type == Type.Empty) {
                    r = line.first { it.type != Type.Empty }
                }
                cell.right = r

                var t = get(col, row - 1)
                if (t.type == Type.Empty) {
                    t = cells.asReversed().first { it[col].type != Type.Empty }[col]
                }
                cell.top = t

                var b = get(col, row + 1)
                if (b.type == Type.Empty) {
                    b = cells.first { it[col].type != Type.Empty }[col]
                }
                cell.bottom = b
            }
        }
    }

    val commands = mutableListOf<Any>()
    var com = input.last()
    while (com.isNotEmpty()) {
        if (com.first().isDigit()) {
            val int = com.takeWhile { it.isDigit() }
            com = com.drop(int.length)
            commands.add(int.toInt())
        } else {
            commands.add(com.first())
            com = com.drop(1)
        }
    }

    var cur = cells.first().first { it.type == Type.Open }
    var dir = Dir.Right
    println(cur)
    commands.forEach {
        if (it is Int) {
            println(it)
            for (i in 0 until it) {
                val next = cur.next(dir)
                if (next.type == Type.Closed) {
                    println("closed")
                    break
                }
                cur = next
                println(cur)
            }
        } else {
            dir = if (it == 'R') {
                dir.cw()
            } else {
                dir.ccw()
            }
            println(dir)
        }
    }

    println(cur)
    val res = 1000 * cur.y + 4 * cur.x + dir.bonus
    return res to 0
}
data class Cell(val type: Type, val x: Int, val y: Int) {
    var top: Cell? = null
    var bottom: Cell? = null
    var left: Cell? = null
    var right: Cell? = null

    fun next(dir: Dir): Cell {
        return when (dir) {
            Dir.Left -> left!!
            Dir.Top -> top!!
            Dir.Right -> right!!
            Dir.Bottom -> bottom!!
        }
    }
}
enum class Type {
    Empty, Open, Closed
}
enum class Dir(val bonus: Int) {
    Left(2), Right(0), Top(3), Bottom(1);

    fun cw(): Dir {
        return when (this) {
            Left -> Top
            Top -> Right
            Right -> Bottom
            Bottom -> Left
        }
    }

    fun ccw(): Dir {
        return when (this) {
            Left -> Bottom
            Top -> Left
            Right -> Top
            Bottom -> Right
        }
    }
}
