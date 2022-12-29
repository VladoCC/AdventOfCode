package d7

import measure
import printResult
import java.io.File

fun main(args: Array<String>) {
    val lines = File("input.txt").readLines()
    printResult(sane(lines))
    measure { sane(lines) }
    printResult(functional(lines))
    measure { functional(lines) }
}

fun functional(input: List<String>): Pair<Any, Any> {
    fun parseCommands(lines: List<String>, commands: List<Command> = emptyList()): List<Command> {
        if (lines.isEmpty()) {
            return commands
        }

        val cmd = lines.first().drop(2)
        val next: List<String>
        val command = if (cmd == "ls") {
            val args = lines.drop(1).takeWhile { !it.startsWith("$") }
            next = lines.drop(args.size + 1)
            Command(cmd, args)
        } else {
            val split = cmd.split(" ")
            next = lines.drop(1)
            Command(split[0], listOf(split[1]))
        }
        return parseCommands(next, commands + command)
    }
    fun List<Command>.split(result: List<List<Command>> = emptyList()): List<List<Command>> {
        if (isEmpty()) {
            return result
        }
        val part = takeWhile { !(it.cmd == "cd" && it.args[0] == "/") }
        val next = drop(part.size + 1)
        return next.split(if (part.isNotEmpty()) result.plusElement(part) else result)
    }
    fun buildFS(commands: List<Command>, current: Dir): Pair<List<Command>, Dir> {
        if (commands.isEmpty()) {
            return commands to current
        }
        with(commands.first()) {
            return if (cmd == "ls") {
                val size = args.filter { !it.startsWith("dir") }.sumOf { it.split(" ")[0].toLong() }
                buildFS(
                    commands.drop(1),
                    current.copy(size = size)
                )
            } else {
                if (args[0] == "..") {
                    return commands.drop(1) to current
                } else {
                    val path = current.path + "/" + args[0]
                    val pair = buildFS(
                        commands.drop(1),
                        current.children.values.firstOrNull { it.path == path }?: Dir(path)
                    )
                    buildFS(pair.first, current.copy(children = current.children + (path to pair.second)))
                }
            }
        }
    }
    fun flatten(dir: Dir): List<Dir> {
        return dir.children.flatMap { flatten(it.value) } + dir
    }

    val root = parseCommands(input).split().map { buildFS(it, Dir("/")) }.map { it.second }.reduce { acc, cur ->
        Dir("/", acc.size + cur.size, acc.children + cur.children)
    }

    val req = root.size() - 40000000
    val files = flatten(root)
    return files.filter { it.size() <= 100000 }.sumOf { it.size() } to files.filter { it.size() > req }.minOf { it.size() }
}
data class Command(val cmd: String, val args: List<String>)
data class Dir(val path: String, val size: Long = 0, val children: Map<String, Dir> = emptyMap(), val parent: Dir? = null) {
    fun size(): Long = size + children.values.sumOf { it.size() }
}

fun sane(input: List<String>): Pair<Any, Any> {
    var current = d7.File("/")
    var i = 0
    val files = mutableMapOf<String, d7.File>()
    files["/"] = current
    val ignore = mutableSetOf<d7.File>()
    while (i < input.size) {
        val line = input[i]
        if (line.startsWith("$ ls")) {
            i++
            while (i < input.size && !input[i].startsWith("$")) {
                val f = input[i]
                if (f.startsWith("dir")) {
                    val path = current.name + "/" + f.drop(4)
                    if (!files.containsKey(path)) {
                        val f = d7.File(path)
                        files[path] = f
                        current.add(f)
                    }
                } else {
                    val split = f.split(" ")
                    val path = current.name + "/" + split[1]
                    if (!files.containsKey(path)) {
                        val f = d7.File(path, split[0].toLong())
                        files[path] = f
                        current.add(f)
                        ignore.add(f)
                    }
                }
                i++
            }
            i--
        } else {
            val move = line.drop(5)
            if (move == "/") {
                current = files["/"]!!
            } else if(move == "..") {
                current = current.parent!!
            } else {
                val path = current.name + "/" + move
                if (files.containsKey(path)) {
                    current = files[path]!!
                } else {
                    val f = d7.File(path)
                    files[path] = f
                    current.add(f)
                    current = f
                }
            }
        }
        i++
    }
    val req = 30000000 - (70000000 - files["/"]!!.size())

    return files.filter { it.value.size() <= 100000 }.filter { it.value !in ignore }.values.sumOf { it.size() } to
            files.values.filter { it.size() > req }.filter { it !in ignore }.minOf { it.size() }
}

class File(val name: String, val size: Long = 0) {
    val children = mutableListOf<d7.File>()
    var parent: d7.File? = null

    fun add(f: d7.File) {
        children.add(f)
        f.parent = this
    }

    fun size(): Long = size + children.sumOf { it.size() }
}