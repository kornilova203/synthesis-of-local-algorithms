package com.github.kornilova_l.benchmarks

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.getVertexRules
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.toDimacs
import org.openjdk.jmh.annotations.*
import java.io.File
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(2)
@Warmup(iterations = 2)
@Measurement(iterations = 8)
open class ToDimacsBenchmark {
    var rules: Set<VertexRule>? = null
    var graph: DirectedGraph? = null

    @Setup
    fun doSetup() {
        rules = getVertexRules("0?1?1")
        graph = DirectedGraph(TileSet(File("generated_tiles/5-7-1.txt")))
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    fun toDimacsBenchmark() {
        toDimacs(graph!!, rules!!)
    }
}