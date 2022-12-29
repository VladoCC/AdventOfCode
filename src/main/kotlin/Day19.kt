package d19

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
    val blueprints = input.map {
        val split = it.split(": ")[1].split(". ")
        Blueprint(
            arrayOf(split[0].dropLast(4).takeLastWhile { it.isDigit() }.toInt(), 0, 0, 0),
            arrayOf(split[1].dropLast(4).takeLastWhile { it.isDigit() }.toInt(), 0, 0, 0),
            arrayOf(split[2].drop(26).takeWhile { it.isDigit() }.toInt(), split[2].dropLast(5).takeLastWhile { it.isDigit() }.toInt(), 0, 0),
            arrayOf(split[3].drop(23).takeWhile { it.isDigit() }.toInt(), 0, split[3].dropLast(10).takeLastWhile { it.isDigit() }.toInt(), 0)
        )
    }

    fun test(res: List<Int>, cost: Array<Int>): Boolean {
        (0 until 4).forEach {
            if (cost[it] > res[it]) {
                return false
            }
        }
        return true
    }
    fun update(res: List<Int>, cost: Array<Int>): List<Int> {
        return res.mapIndexed { index, i -> i + cost[index] }
    }

    val res = blueprints.map { blueprint ->
        val dyn = (0 until 24).map { mutableListOf<State>() }
        dyn[0].add(State(listOf(0, 0, 0, 0), arrayOf(1, 0, 0, 0)))
        (0 until 23).forEach {
            println(it)
            dyn[it].forEach {state ->
                val res = state.resources.mapIndexed { index, i -> i + state.machines[index] }
                if (test(res, blueprint.geode)) {
                    val new = state.machines.clone()
                    new[3]++
                    dyn[it + 1].add(State(update(res, blueprint.geode), new))
                } else {
                    dyn[it + 1].add(State(res, state.machines))
                    if (test(res, blueprint.ore)) {
                        val new = state.machines.clone()
                        new[0]++
                        dyn[it + 1].add(State(update(res, blueprint.ore), new))
                    }
                    if (test(res, blueprint.clay)) {
                        val new = state.machines.clone()
                        new[1]++
                        dyn[it + 1].add(State(update(res, blueprint.clay), new))
                    }
                    if (test(res, blueprint.obsidian)) {
                        val new = state.machines.clone()
                        new[2]++
                        dyn[it + 1].add(State(update(res, blueprint.obsidian), new))
                    }
                }
            }
        }
        dyn[23].map {
            it.resources[3] + it.machines[3]
        }
    }.mapIndexed { index, ints ->
        (index + 1) * ints.max()
    }
    return res to 0
}
data class Blueprint(val ore: Array<Int>, val clay: Array<Int>, val obsidian: Array<Int>, val geode: Array<Int>)
data class State(val resources: List<Int>, val machines: Array<Int>)