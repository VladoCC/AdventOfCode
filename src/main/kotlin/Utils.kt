fun measure(f: () -> Unit) {
    val start = System.currentTimeMillis()
    (0..1_000).forEach {
        f()
    }
    val time = (System.currentTimeMillis() - start) / 1_000f
    println("Time: $time ms")
}

fun printResult(res: Pair<Any, Any>) {
    println("Part 1: ${res.first}")
    println("Part 2: ${res.second}")
}