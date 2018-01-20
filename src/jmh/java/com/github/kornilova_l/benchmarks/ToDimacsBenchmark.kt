package com.github.kornilova_l.benchmarks

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.patternToProblem
import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.toDimacs
import org.openjdk.jmh.annotations.*
import java.io.File
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(2)
@Warmup(iterations = 2)
@Measurement(iterations = 8)
open class ToDimacsBenchmark {
    private var rules: Set<VertexRule> = patternToProblem("0?1?1")
    private var graph = DirectedGraph.createInstance(File("directed_graphs/5-7-1.graph"))

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    fun toDimacsBenchmark() {
        toDimacs(graph, rules)
    }
}